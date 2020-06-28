package com.dvproject.vertTerm.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletResponse;

import com.dvproject.vertTerm.Model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import com.dvproject.vertTerm.exception.ProcedureException;
import com.dvproject.vertTerm.exception.ProcedureRelationException;
import com.dvproject.vertTerm.repository.AppointmentRepository;
import com.dvproject.vertTerm.repository.AppointmentgroupRepository;
import com.dvproject.vertTerm.repository.UserRepository;

@Service
public class AppointmentgroupServiceImpl implements AppointmentgroupService {
	@Autowired
	private AppointmentgroupRepository appointmentgroupRepository;

	@Autowired
	private AppointmentServiceImpl appointmentService;

	@Autowired
	private AppointmentRepository appointmentRepository;

	@Autowired
	private ProcedureService procedureService;

	@Autowired
	private ResourceService resourceService;

	@Autowired
	private EmployeeService employeeService;

	@Autowired
	private CustomerService customerService;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private UserService userService;

	@Autowired
	private RestrictionService restrictionService;

	@Autowired
	private HttpServletResponse httpResponse;

	@Override
	public List<Appointmentgroup> getAll() {
		return appointmentgroupRepository.findAll();
	}

	@Override
	public List<Appointmentgroup> getAppointmentgroupsWithStatus(Status status) {
		return appointmentgroupRepository.findByStatus(status);
	}

	@Override
	public Appointmentgroup getAppointmentgroupContainingAppointmentID(String id) {
		if (id == null) {
			throw new NullPointerException("The id of the given appointment is null");
		}
		if (appointmentService.getById(id) == null) {
			throw new ResourceNotFoundException("The id of the given appointment is invalid");
		}

		return appointmentgroupRepository.findByAppointmentsId(id);
	}

	@Override
	public Appointmentgroup getOptimizedSuggestion(Appointmentgroup appointmentgroup,
			Optimizationstrategy optimizationstrategy) {
		// TODO
		return null;
	}

	@Override
	public Appointmentgroup getById(String id) {
		return this.getAppointmentInternal(id);
	}

	@Override
	public void setPullableAppointment(Appointment appointment) {
		if (isPullable(appointment)) {
			httpResponse.setHeader("appointmentid", appointment.getId());
			httpResponse.setHeader("starttime", getStringRepresentationOf(appointment.getPlannedStarttime()));
		}
	}

	@Override
	public void setPullableAppointment() {
		Date startdate = getDateOfNowRoundedUp();
		List<Appointment> appointmentsToPull = getPullableAppointments(startdate);

		httpResponse.addHeader("starttime", getStringRepresentationOf(startdate));
		appointmentsToPull.forEach(appointment -> httpResponse.addHeader("appointmentid", appointment.getId()));
	}

	@Override
	public Appointmentgroup create(Appointmentgroup newInstance) {
		if (!appointmentgroupRepository.existsById(newInstance.getId())) {
			List<Appointment> appointments = newInstance.getAppointments();

			for (Appointment appointment : appointments) {
				if (appointment.getPlannedStarttime() != null || appointment.getPlannedEndtime() != null
						|| appointment.getActualStarttime() != null || appointment.getActualEndtime() != null) {
					throw new IllegalArgumentException(
							"Appointments have already been booked, no new appointmentgroup can be created with them");
				}
			}

			return appointmentgroupRepository.save(newInstance);
		}
		return null;
	}

	@Override
	public Appointmentgroup update(Appointmentgroup updatedInstance) {
		if (appointmentgroupRepository.existsById(updatedInstance.getId())) {
			if (!StatusService.isUpdateable(updatedInstance.getStatus())) {
				throw new IllegalArgumentException("The given procedure is not updateable");
			}
			return appointmentgroupRepository.save(updatedInstance);
		}
		return null;
	}

	/**
	 * 
	 * @return true if the appointmentgroup has been booked
	 * 
	 * @exception RuntimeException if a value of the appointmentgroup does not
	 *                             conform to the conditions
	 */
	@Override
	public User bookAppointmentgroup(String userid, Appointmentgroup appointmentgroup, boolean override) {
		List<Appointment> appointments = appointmentgroup.getAppointments();
		boolean noUserAttached = userid == null || userid.equals("");
		Customer user = noUserAttached ? customerService.getAnonymousUser() : customerService.getById(userid);

		for (Appointment appointment : appointments) {
			if (!noUserAttached && appointment.getBookedCustomer() != null
					&& !appointment.getBookedCustomer().getId().equals(userid)) {

				if (override) {
					appointment.addWarning(Warning.USER_WARNING);
				} else {
					throw new IllegalArgumentException(
							"User in the appointment for the procedure " + appointment.getBookedProcedure().getId()
									+ " does not conform to the given user for all appointments");
				}
			} else {
				appointment.setBookedCustomer(user);
			}

			try {
				loadAppointmentdataFromDatabase(appointment);
			} catch (ProcedureException ex) {
				if (!override)
					throw ex;

				appointment.addWarning(Warning.PROCEDURE_WARNING);
			}
		}

		if (override) {
			try {
				appointmentgroup.testProcedureRelations();
			} catch (ProcedureRelationException ex) {
				appointmentgroup.getAppointments()
						.forEach(appointment -> appointment.addWarning(Warning.PROCEDURE_RELATION_WARNING));
			}

			appointmentgroup.testBookability(restrictionService, appointmentService, new OverrideBookingTester(new ArrayList<>()));
		} else {
			appointmentgroup.testProcedureRelations();
			appointmentgroup.testBookability(restrictionService, appointmentService, new NormalBookingTester(new ArrayList<>()));
		}

		if (noUserAttached) {
			userRepository.save(user);
		}

		// create all appointments of the appointmentgroup
		for (Appointment appointment : appointments) {
			appointment.setStatus(AppointmentStatus.PLANNED);
			appointmentRepository.save(appointment);
		}

		appointmentgroup.setStatus(Status.ACTIVE);
		appointmentgroupRepository.save(appointmentgroup);

		return noUserAttached ? user : null;
	}

	@Override
	public boolean startAppointment(String appointmentid) {
		Appointment appointment = appointmentService.getById(appointmentid);

		if (hasActualTimeValue(appointment)) {
			throw new UnsupportedOperationException("You can not start an appointment that has already been started");
		}

		appointment.setActualStarttime(getDateOfNow());

		return appointmentService.update(appointment).getActualStarttime() != null;
	}

	@Override
	public boolean stopAppointment(String appointmentid) {
		Appointment appointment = appointmentService.getById(appointmentid);

		if (!hasBeenStarted(appointment)) {
			throw new UnsupportedOperationException("You can only stop an appointment that has already been started");
		}

		appointment.setActualEndtime(getDateOfNow());
		appointment.setStatus(AppointmentStatus.DONE);

		appointmentService.update(appointment);
		appointment = appointmentService.getById(appointmentid);

		if (appointment.getActualEndtime().before(appointment.getPlannedEndtime()))
			setPullableAppointment();

		return appointment.getActualEndtime() != null && appointment.getStatus() == AppointmentStatus.DONE;
	}

	@Override
	public boolean delete(String id) {
		this.deleteAppointmentgroup(id);

		Appointmentgroup appointmentgroup = this.getAppointmentInternal(id);

		return appointmentgroup.getStatus() == Status.DELETED;
	}

	@Override
	public boolean deleteAppointment(String id, boolean override) {
		Appointment appointment = appointmentService.getById(id);
		Appointmentgroup appointmentgroupOfAppointment = getAppointmentgroupContainingAppointmentID(id);
		List<Appointment> appointments = appointmentgroupOfAppointment.getAppointments();
		boolean retVal = false;

		appointments.removeIf(app -> app.getId().equals(appointment.getId()));

		try {
			appointmentgroupOfAppointment.testProcedureRelations();
		} catch (ProcedureException ex) {
			appointments.forEach(app -> app.addWarning(Warning.PROCEDURE_WARNING, Warning.PROCEDURE_RELATION_WARNING));
		} catch (ProcedureRelationException ex) {
			appointments.forEach(app -> app.addWarning(Warning.PROCEDURE_RELATION_WARNING));
		} catch (RuntimeException ex) {
			if (!override)
				throw ex;
		}

		retVal = appointmentService.delete(id);

		setPullableAppointment();

		return retVal;
	}

	private boolean isPullable(Appointment appointment) {
		if (!appointment.isCustomerIsWaiting())
			return false;

		BookingTester tester = new NormalBookingTester();
		List<TimeInterval> timeIntervals = new ArrayList<>();
		Appointmentgroup appointmentgroup = this.getAppointmentgroupContainingAppointmentID(appointment.getId());
		List<Appointment> appointments = appointmentgroup.getAppointments();

		appointments.removeIf(app -> app.getId().equals(appointment.getId()));
		appointments.add(appointment);

		try {
			appointmentgroup.testProcedureRelations();
			tester.setAppointment(appointment);

			appointments.forEach(app -> new NormalBookingTester(app).testAppointmentTimes(timeIntervals));

			tester.testAll(appointmentService, restrictionService, new ArrayList<>());
		} catch (RuntimeException ex) {
			return false;
		}

		return true;
	}

	private Appointmentgroup deleteAppointmentgroup(String id) {
		Appointmentgroup appointmentgroup = this.getAppointmentInternal(id);
		appointmentgroup.setStatus(Status.DELETED);
		return appointmentgroupRepository.save(appointmentgroup);
	}

	private Appointmentgroup getAppointmentInternal(String id) {
		if (id == null) {
			throw new ResourceNotFoundException("The id of the given appointmentgroup is null");
		}

		Optional<Appointmentgroup> appointmentgroup = appointmentgroupRepository.findById(id);

		if (appointmentgroup.isPresent()) {
			return appointmentgroup.get();
		} else {
			throw new ResourceNotFoundException("No appointmentgroup with the given id (" + id + ") can be found.");
		}
	}

	private void loadAppointmentdataFromDatabase(Appointment appointment) {
		Procedure procedure = procedureService.getById(appointment.getBookedProcedure().getId());
		List<Employee> employees = new ArrayList<>();
		List<Resource> resources = new ArrayList<>();

		// populate list of employees
		appointment.getBookedEmployees().forEach(employee -> employees.add(employeeService.getById(employee.getId())));
		// set employees of appointment
		appointment.setBookedEmployees(employees);

		// populate list of resources
		appointment.getBookedResources().forEach(resource -> resources.add(resourceService.getById(resource.getId())));
		// set resources of appointment
		appointment.setBookedResources(resources);

		appointment.setBookedProcedure(procedure);

		if (hasActualTimeValue(appointment)) {
			throw new ProcedureException("Appointment of an procedure contains actual times", procedure);
		}
	}

	private List<Appointment> getPullableAppointments(Date startdate) {
		List<Appointment> appointmentsToTest = appointmentService.getAppointmentsInTimeIntervalWithStatus(startdate,
				getLatestTimeOfToday(), AppointmentStatus.PLANNED);

		appointmentsToTest.removeIf(app -> {
			app.setPlannedStarttime(startdate);
			app.setPlannedEndtime(app.generatePlannedEndtime());

			return !this.isPullable(app);
		});

		return appointmentsToTest;
	}

	private boolean hasActualTimeValue(Appointment appointment) {
		return appointment.getActualStarttime() != null || appointment.getActualEndtime() != null;
	}

	private boolean hasBeenStarted(Appointment appointment) {
		return appointment.getActualStarttime() != null && appointment.getActualEndtime() == null;
	}

	private Date getDateOfNow() {
		LocalDateTime nowInOtherTimeZone = LocalDateTime.ofInstant(Instant.now(), ZoneId.of("CET"));
		return Date.from(nowInOtherTimeZone.atZone(ZoneId.systemDefault()).toInstant());
	}

	private Date getDateOfNowRoundedUp() {
		int addedMinutes = 2;
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.MINUTE, cal.get(Calendar.MINUTE) + addedMinutes);
		cal.set(Calendar.SECOND, 0);

		return cal.getTime();
	}

	private Date getLatestTimeOfToday() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);

		return calendar.getTime();
	}

	private String getStringRepresentationOf(Date date) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
		LocalDateTime ldt = date.toInstant().atZone(ZoneId.of("UTC")).toLocalDateTime();

		return formatter.format(ldt);
	}

}

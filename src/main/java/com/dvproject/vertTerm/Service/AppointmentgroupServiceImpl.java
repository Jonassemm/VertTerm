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
import java.util.TimeZone;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.dvproject.vertTerm.Model.Appointment;
import com.dvproject.vertTerm.Model.AppointmentStatus;
import com.dvproject.vertTerm.Model.Appointmentgroup;
import com.dvproject.vertTerm.Model.BookingTester;
import com.dvproject.vertTerm.Model.Employee;
import com.dvproject.vertTerm.Model.NormalBookingTester;
import com.dvproject.vertTerm.Model.Optimizationstrategy;
import com.dvproject.vertTerm.Model.OverrideBookingTester;
import com.dvproject.vertTerm.Model.Procedure;
import com.dvproject.vertTerm.Model.Resource;
import com.dvproject.vertTerm.Model.Status;
import com.dvproject.vertTerm.Model.TimeInterval;
import com.dvproject.vertTerm.Model.User;
import com.dvproject.vertTerm.Model.Warning;
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
	private UserRepository userRepository;

	@Autowired
	private UserService userService;

	@Autowired
	private RestrictionService restrictionService;

	@Autowired
	private HttpServletResponse httpResponse;

	@Override
//	@PreAuthorize("hasAuthority('APPOINTMENT_READ')")
	public List<Appointmentgroup> getAll() {
		return appointmentgroupRepository.findAll();
	}

	@Override
//	@PreAuthorize("hasAuthority('APPOINTMENT_READ')")
	public List<Appointmentgroup> getAppointmentgroupsWithStatus(Status status) {
		return appointmentgroupRepository.findByStatus(status);
	}

	@Override
//	@PreAuthorize("hasAuthority('APPOINTMENT_READ')")
	public Appointmentgroup getAppointmentgroupContainingAppointmentID(String id) {
		if (id == null)
			throw new NullPointerException("The id of the given appointment is null");

		if (appointmentService.getById(id) == null)
			throw new ResourceNotFoundException("The id of the given appointment is invalid");

		return appointmentgroupRepository.findByAppointmentsId(id);
	}

	@Override
//	@PreAuthorize("hasAuthority('APPOINTMENT_READ')")
	public Appointmentgroup getOptimizedSuggestion(Appointmentgroup appointmentgroup,
			Optimizationstrategy optimizationstrategy) {
		// TODO
		return null;
	}

	@Override
//	@PreAuthorize("hasAuthority('APPOINTMENT_READ')")
	public Appointmentgroup getById(String id) {
		return getAppointmentgroupInternal(id);
	}

	@Override
	public void testWarnings(String appointmentid) {
		Appointmentgroup appointmentgroup = getUpdatableAppointmentgroupContainingAppointmentID(appointmentid);
		if (appointmentid != null) {
			List<Appointment> appointments = appointmentgroup.getAppointments();
			BookingTester tester = new OverrideBookingTester(new ArrayList<>());

			appointmentgroup.resetAllWarnings();

			appointments.forEach(appointment -> appointmentRepository
					.save(tester.testAll(appointment, appointmentService, restrictionService)));
		}
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

		if (appointmentsToPull != null && appointmentsToPull.size() > 0) {
			httpResponse.addHeader("starttime", getStringRepresentationOf(startdate));
			appointmentsToPull.forEach(appointment -> httpResponse.addHeader("appointmentid", appointment.getId()));
		}
	}

	@Override
	@PreAuthorize("hasAuthority('APPOINTMENT_WRITE')")
	public Appointmentgroup create(Appointmentgroup newInstance) {
		if (!appointmentgroupRepository.existsById(newInstance.getId())) {
			List<Appointment> appointments = newInstance.getAppointments();

			for (Appointment appointment : appointments) {
				if (appointment.getPlannedStarttime() != null || appointment.getPlannedStarttime() != null
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
	public String bookAppointmentgroup(String userid, Appointmentgroup appointmentgroup, boolean override) {
		List<Appointment> appointments = appointmentgroup.getAppointments();
		boolean noUserAttached = userid == null || userid.equals("");
		User user = noUserAttached ? userService.getAnonymousUser() : userService.getById(userid);
		String link = null;

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
			testWarningsForAppointmentGroup(appointmentgroup);
		} else {
			appointmentgroup.testProcedureRelations();
			appointmentgroup.testBookability(restrictionService, appointmentService,
					new NormalBookingTester(new ArrayList<>()));
		}

		if (noUserAttached) {
			link = user.generateLoginLink();
			userService.encodePassword(user);
			userRepository.save(user);
		}

		// create all appointments of the appointmentgroup
		for (Appointment appointment : appointments) {
			appointment.setStatus(AppointmentStatus.PLANNED);
			appointmentRepository.save(appointment);
		}

		appointmentgroup.setStatus(Status.ACTIVE);
		appointmentgroupRepository.save(appointmentgroup);

		return link;
	}

	@Override
	public boolean startAppointment(String appointmentid) {
		Appointment appointment = appointmentService.getById(appointmentid);

		if (hasActualTimeValue(appointment))
			throw new UnsupportedOperationException("You can not start an appointment that has already been started");

		appointment.setActualStarttime(getDateOfNow());

		appointmentService.update(appointment);
		appointment = appointmentService.getById(appointmentid);

		return appointment.getActualStarttime() != null;
	}

	@Override
	public boolean stopAppointment(String appointmentid) {
		Appointment appointment = appointmentService.getById(appointmentid);

		if (!hasBeenStarted(appointment))
			throw new UnsupportedOperationException("You can only stop an appointment that has already been started");

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
		deleteAppointmentgroupInternal(id);

		return getAppointmentgroupInternal(id).getStatus() == Status.DELETED;
	}

	@Override
	public boolean deleteAppointment(String id, boolean override) {
		Appointmentgroup appointmentgroupOfAppointment = getAppointmentgroupContainingAppointmentID(id);
		List<Appointment> appointmentsOfAppointmentgroup = appointmentgroupOfAppointment.getAppointments();
		Appointment appointment = appointmentsOfAppointmentgroup.stream().filter(app -> app.getId().equals(id)).findAny()
				.get();
		List<Appointment> appointmentsToTestWarningsFor = null;
		boolean retVal = false;
		AppointmentStatus status = appointment.getStatus();

		retVal = appointmentService.delete(id);

		if (override) {
			testWarningsForAppointmentGroup(appointmentgroupOfAppointment);
			appointment.setWarnings(new ArrayList<>());
			appointmentsOfAppointmentgroup.forEach(app -> appointmentService.update(app));
		} else {
			try {
				appointmentgroupOfAppointment.testProcedureRelations();
			} catch (ProcedureException | ProcedureRelationException ex) {
				// set the appointment to the previous status
				appointment = appointmentService.getById(id);
				appointment.setStatus(status);
				appointmentService.update(appointment);
			}
		}

		appointmentsToTestWarningsFor = appointmentService.getOverlappingAppointmentsInTimeInterval(
				appointment.getPlannedStarttime(), appointment.getPlannedEndtime(), AppointmentStatus.PLANNED);

		testWarningsForAppointments(appointmentsToTestWarningsFor);

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

	private Appointmentgroup deleteAppointmentgroupInternal(String id) {
		Appointmentgroup appointmentgroup = this.getAppointmentgroupInternal(id);
		appointmentgroup.setStatus(Status.DELETED);
		return appointmentgroupRepository.save(appointmentgroup);
	}

	private Appointmentgroup getAppointmentgroupInternal(String id) {
		if (id == null)
			throw new ResourceNotFoundException("The id of the given appointmentgroup is null");

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
		Date enddate = getLatestTimeOfToday();
		List<Appointment> appointmentsToTest = appointmentService.getAppointmentsInTimeIntervalAndStatus(startdate,
				enddate, AppointmentStatus.PLANNED);

		appointmentsToTest.removeIf(app -> {
			app.setPlannedEndtime(app.generatePlannedEndtime(startdate));
			app.setPlannedStarttime(startdate);

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
		Calendar cal = getCalendar();
		cal.set(Calendar.MINUTE, cal.get(Calendar.MINUTE) + addedMinutes);
		cal.set(Calendar.SECOND, 0);

		return cal.getTime();
	}

	private Date getLatestTimeOfToday() {
		Calendar calendar = getCalendar();
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);

		return calendar.getTime();
	}

	private Calendar getCalendar() {
		Calendar cal = Calendar.getInstance(TimeZone.getDefault());
		cal.setTime(getDateOfNow());
		return cal;
	}

	private String getStringRepresentationOf(Date date) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
		LocalDateTime ldt = date.toInstant().atZone(ZoneId.of("UTC")).toLocalDateTime();

		return formatter.format(ldt);
	}

	private void testWarningsForAppointments(List<Appointment> appointmentsToTest) {
		List<String> appointmentIdsTested = new ArrayList<>();

		for (Appointment appointmentToTest : appointmentsToTest) {
			String appointmentIdToTest = appointmentToTest.getId();
			Appointmentgroup appointmentgroupToTest = null;
			List<Appointment> appointmentsOfAppointmentgroupToTest = null;

			if (appointmentIdsTested.contains(appointmentIdToTest))
				continue;

			appointmentgroupToTest               = getAppointmentgroupContainingAppointmentID(appointmentIdToTest);
			appointmentsOfAppointmentgroupToTest = appointmentgroupToTest.getAppointments();

			appointmentgroupToTest.resetAllWarnings();

			testWarningsForAppointmentGroup(appointmentgroupToTest);

			appointmentsOfAppointmentgroupToTest.forEach(app -> appointmentService.update(app));

			// set ids of the tested appointments
			appointmentsOfAppointmentgroupToTest.forEach(app -> appointmentIdsTested.add(app.getId()));
		}
	}

	private void testWarningsForAppointmentGroup(Appointmentgroup appointmentgroup) {
		List<Appointment> appointmentsOfAppointmentgroup = appointmentgroup.getAppointments();
		appointmentgroup.resetAllWarnings();

		try {
			appointmentgroup.testProcedureRelations();
		} catch (ProcedureException ex) {
			appointmentsOfAppointmentgroup
					.forEach(app -> app.addWarning(Warning.PROCEDURE_WARNING, Warning.PROCEDURE_RELATION_WARNING));
		} catch (ProcedureRelationException ex) {
			appointmentsOfAppointmentgroup.forEach(app -> app.addWarning(Warning.PROCEDURE_RELATION_WARNING));
		}

		appointmentgroup.testBookability(restrictionService, appointmentService,
				new OverrideBookingTester(new ArrayList<>()));
	}

	private Appointmentgroup getUpdatableAppointmentgroupContainingAppointmentID(String appointmentid) {
		Appointmentgroup appointmentgroup = this.getAppointmentgroupContainingAppointmentID(appointmentid);
		List<Appointment> appointments = appointmentgroup.getAppointments();
		boolean isUpdateble = appointmentgroup.getStatus() == Status.ACTIVE;

		isUpdateble &= appointments.stream()
				.noneMatch(app -> hasActualTimeValue(app) && app.getStatus() == AppointmentStatus.DELETED);

		return isUpdateble ? appointmentgroup : null;
	}

}

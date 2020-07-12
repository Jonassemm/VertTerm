package com.dvproject.vertTerm.Service;

import java.security.Principal;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.dvproject.vertTerm.Model.*;
import com.dvproject.vertTerm.exception.*;
import com.dvproject.vertTerm.repository.*;

/**
 * @author Joshua Müller
 */
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
	@PreAuthorize("hasAuthority('APPOINTMENT_READ')")
	public List<Appointmentgroup> getAll() {
		return appointmentgroupRepository.findAll();
	}

	@Override
	@PreAuthorize("hasAuthority('APPOINTMENT_READ')")
	public List<Appointmentgroup> getAppointmentgroupsWithStatus(Status status) {
		return appointmentgroupRepository.findByStatus(status);
	}

	@Override
	@PreAuthorize("hasAuthority('APPOINTMENT_READ')")
	public Appointmentgroup getAppointmentgroupContainingAppointmentID(String id) {
		if (id == null)
			throw new NullPointerException("The id of the given appointment is null");

		if (appointmentService.getById(id) == null)
			throw new ResourceNotFoundException("The id of the given appointment is invalid");

		return appointmentgroupRepository.findByAppointmentsId(id);
	}

	@Override
	@PreAuthorize("hasAuthority('APPOINTMENT_READ')")
	public Appointmentgroup getById(String id) {
		return getAppointmentgroupInternal(id);
	}

	@Override
	@PreAuthorize("hasAuthority('APPOINTMENT_WRITE')")
	public void testWarnings(String appointmentid) {
		if (appointmentid != null) {
			Appointmentgroup appointmentgroup = getUpdatableAppointmentgroupContainingAppointmentID(appointmentid);
			testWarningsForAppointmentgroup(appointmentgroup.getId());
		}
	}

	@Override
	@PreAuthorize("hasAuthority('APPOINTMENT_WRITE')")
	public void testWarningsForAppointmentgroup(String appointmentgroupid) {
		Appointmentgroup appointmentgroup = getAppointmentgroupInternal(appointmentgroupid);
		BookingTester tester = new OverrideBookingTester(new ArrayList<>());

		testWarningsForAppointmentGroup(appointmentgroup, tester);

		saveAppointmentgroupInternal(appointmentgroup);
	}

	@Override
	@PreAuthorize("hasAuthority('APPOINTMENT_WRITE')")
	public void testWarningsForAppointments(List<Appointment> appointmentsToTest) {
		appointmentsToTest = appointmentService.cleanseAppointmentsOfBlocker(appointmentsToTest);
		List<String> appointmentIdsTested = new ArrayList<>();

		for (Appointment appointmentToTest : appointmentsToTest) {
			String appointmentIdToTest = appointmentToTest.getId();

			// has already been tested
			if (appointmentIdsTested.contains(appointmentIdToTest))
				continue;

			Appointmentgroup appointmentgroupToTest = getAppointmentgroupContainingAppointmentID(appointmentIdToTest);
			List<Appointment> appointmentsOfAppointmentgroupToTest = appointmentgroupToTest.getAppointments();

			testWarningsForAppointmentGroup(appointmentgroupToTest);

			saveAppointmentgroupInternal(appointmentgroupToTest);

			// set ids of the tested appointments
			appointmentsOfAppointmentgroupToTest.forEach(app -> appointmentIdsTested.add(app.getId()));
		}
	}

	@Override
	public void canBookAppointments(Principal user, Appointmentgroup appointmentgroup) {
		// get all procedures from database
		List<Procedure> procedures = appointmentgroup.getAppointments().stream()
				.map(app -> procedureService.getById(app.getBookedProcedure().getId())).collect(Collectors.toList());
		// tests whether or not the given principal is an employee
		boolean isEmployee = user != null && employeeService.getByUsername(user.getName()) != null;
		// if procedures contain at least one privateProcedure, get that
		Procedure privateProcedure = procedures.stream().filter(procedure -> !procedure.isPublicProcedure()).findAny()
				.orElse(null);
		boolean notBookable = !isEmployee && privateProcedure != null;

		if (notBookable)
			throw new ProcedureException(
					"At least one appointment contains a non-public procedure that a non-employee tries to book",
					privateProcedure);
	}

	@Override
	public void setPullableAppointment(Appointment appointment) {
		Date starttime = getDateOfNowRoundedUp();
		appointment.generateNewDatesFor(starttime);

		if (isPullable(appointment)) {
			httpResponse.setHeader("appointmentid", appointment.getId());
			httpResponse.setHeader("starttime", getStringRepresentation(appointment.getPlannedStarttime()));
		}
	}

	@Override
	public void setPullableAppointments(Appointment appointment) {
		Date starttime = getDateOfNowRoundedUp();
		List<Appointment> appointmentsToPull = getPullableAppointments(starttime, appointment);
		boolean listIsSet = appointmentsToPull != null && appointmentsToPull.size() > 0;

		if (listIsSet) {
			httpResponse.addHeader("starttime", getStringRepresentation(starttime));
			appointmentsToPull.forEach(app -> httpResponse.addHeader("appointmentid", app.getId()));
		}
	}

	/**
	 * 
	 * @return true if the appointmentgroup has been booked
	 * 
	 * @exception RuntimeException if a value of the appointmentgroup does not
	 *                             conform to the conditions
	 */
	@Override
	@PreAuthorize("hasAuthority('APPOINTMENT_WRITE')")
	public String bookAppointmentgroup(String userid, Appointmentgroup appointmentgroup, boolean override) {
		List<Appointment> appointments = appointmentgroup.getAppointments();
		boolean noUserAttached = userid == null || userid.equals("");
		User user = noUserAttached ? userService.getAnonymousUser() : userService.getById(userid);
		boolean attachedUserNotActive = !noUserAttached && !user.getSystemStatus().isActive();
		BookingTester tester = override ? new OverrideBookingTester(new ArrayList<>())
				: new NormalBookingTester(new ArrayList<>());

		if (attachedUserNotActive)
			throw new IllegalArgumentException("User is not active");

		appointmentgroup.setStatus(Status.ACTIVE);

		for (Appointment appointment : appointments) {
			loadAppointmentdataFromDatabaseWithOverride(appointment, override);
			testUserdataInAppointmentWithOverride(appointment, user, noUserAttached, override);
			appointment.setStatus(AppointmentStatus.PLANNED);
		}

		if (!appointmentgroup.hasDistinctProcedures())
			throw new IllegalArgumentException("Appointments contain duplicate procedures or procedures with id == null");

		appointmentgroup.testProcedureRelations(override);
		appointmentgroup.testBookability(restrictionService, appointmentService, tester);

		String link = noUserAttached ? generateLoginLink(user) : null;

		saveAppointmentgroupInternal(appointmentgroup);

		return link;
	}

	@Override
	@PreAuthorize("hasAuthority('APPOINTMENT_WRITE')")
	public boolean startAppointment(String appointmentid) {
		Appointment appointment = appointmentService.getById(appointmentid);

		if (hasActualTimeValue(appointment))
			throw new UnsupportedOperationException("You can not start an appointment that has already been started");

		appointment.setActualStarttime(getDateOfNow());

		appointment = appointmentService.update(appointment);

		return appointment.getActualStarttime() != null;
	}

	@Override
	@PreAuthorize("hasAuthority('APPOINTMENT_WRITE')")
	public boolean stopAppointment(String appointmentid) {
		Appointment appointment = appointmentService.getById(appointmentid);

		if (!hasBeenStarted(appointment))
			throw new UnsupportedOperationException("You can only stop an appointment that has already been started");

		appointment.setActualEndtime(getDateOfNow());
		appointment.setStatus(AppointmentStatus.DONE);

		appointment = appointmentService.update(appointment);

		if (appointment.getActualEndtime().before(appointment.getPlannedEndtime()))
			setPullableAppointments(appointment);

		return appointment.getActualEndtime() != null && appointment.getStatus() == AppointmentStatus.DONE;
	}

	@Override
	@PreAuthorize("hasAuthority('APPOINTMENT_WRITE')")
	public boolean delete(String id) {
		Appointmentgroup appointmentgroup = deleteAppointmentgroupInternal(id);

		return appointmentgroup.getStatus().isDeleted();
	}

	@Override
	@PreAuthorize("hasAuthority('APPOINTMENT_WRITE')")
	public boolean deleteAppointment(String id, boolean override) {
		Appointmentgroup appointmentgroup = getAppointmentgroupContainingAppointmentID(id);
		List<Appointment> appointments = appointmentgroup.getAppointments();
		Appointment appointment = appointments.stream().filter(app -> app.getId().equals(id)).findAny()
				.get();

		appointments.remove(appointment);

		if (override) {
			testWarningsForAppointmentGroup(appointmentgroup);
			appointment.setWarnings(new ArrayList<>());
			saveAppointmentgroupInternal(appointmentgroup);
		} else {
			appointmentgroup.testProcedureRelations(override);
		}

		boolean retVal                        = appointmentService.delete(id);

		List<Appointment> appointmentsToTest = appointmentService.getOverlappingAppointmentsInTimeInterval(
				appointment.getPlannedStarttime(), appointment.getPlannedEndtime(), AppointmentStatus.PLANNED);

		testWarningsForAppointments(appointmentsToTest);

		setPullableAppointments(appointment);

		return retVal;
	}

	private boolean isPullable(Appointment appointment) {
		if (!appointment.isCustomerIsWaiting())
			return false;

		BookingTester tester = new NormalBookingTester();
		List<TimeInterval> timeIntervals = new ArrayList<>();
		Appointmentgroup appointmentgroup = getAppointmentgroupContainingAppointmentID(appointment.getId());
		List<Appointment> appointments = appointmentgroup.getAppointments();
		boolean override = false;

		appointments.removeIf(app -> app.getId().equals(appointment.getId()));
		appointments.add(appointment);

		try {
			appointmentgroup.testProcedureRelations(override);
			tester.testAll(appointment, appointmentService, restrictionService, timeIntervals);
		} catch (RuntimeException ex) {
			return false;
		}

		return true;
	}

	private Appointmentgroup deleteAppointmentgroupInternal(String id) {
		Appointmentgroup appointmentgroup = getAppointmentgroupInternal(id);
		appointmentgroup.setStatus(Status.DELETED);
		return appointmentgroupRepository.save(appointmentgroup);
	}

	private Appointmentgroup getAppointmentgroupInternal(String id) {
		if (id == null)
			throw new ResourceNotFoundException("The id of the given appointmentgroup is null");

		Optional<Appointmentgroup> appointmentgroup = appointmentgroupRepository.findById(id);

		if (appointmentgroup.isPresent()) {
			return appointmentgroup.get();
		} else
			throw new ResourceNotFoundException("No appointmentgroup with the given id (" + id + ") can be found.");
	}

	private void loadAppointmentdataFromDatabaseWithOverride(Appointment appointment, boolean override) {
		try {
			loadAppointmentdataFromDatabase(appointment);
		} catch (ProcedureException ex) {
			if (!override)
				throw ex;

			appointment.addWarning(Warning.PROCEDURE_WARNING);
		}
	}

	private void loadAppointmentdataFromDatabase(Appointment appointment) {
		Procedure procedure = procedureService.getById(appointment.getBookedProcedure().getId());
		List<Employee> employees = new ArrayList<>();
		List<Resource> resources = new ArrayList<>();

		// populate list of employees
		appointment.getBookedEmployees().stream().filter(employee -> employee.getId() != null)
				.forEach(employee -> employees.add(employeeService.getById(employee.getId())));
		// set employees of appointment
		appointment.setBookedEmployees(employees);

		// populate list of resources
		appointment.getBookedResources().stream().filter(resource -> resource.getId() != null)
				.forEach(resource -> resources.add(resourceService.getById(resource.getId())));
		// set resources of appointment
		appointment.setBookedResources(resources);

		appointment.setBookedProcedure(procedure);

		if (hasActualTimeValue(appointment))
			throw new ProcedureException("Appointment of an procedure contains actual times", procedure);
	}

	private List<Appointment> getPullableAppointments(Date startdate, Appointment appointment) {
		List<Appointment> appointmentsToTest;

		if (appointment == null) {
			appointmentsToTest = appointmentService.getAppointmentsInTimeIntervalAndStatus(startdate,
					getLatestTimeOfToday(), AppointmentStatus.PLANNED);
		} else {
			// create object-ids from the id strings because mongodb can not handle a list
			// of strings
			List<ObjectId> employeeids = appointment.getBookedEmployees().stream().map(emp -> new ObjectId(emp.getId()))
					.collect(Collectors.toList());
			List<ObjectId> resourceids = appointment.getBookedResources().stream().map(res -> new ObjectId(res.getId()))
					.collect(Collectors.toList());

			appointmentsToTest = appointmentService.getAppointmentsWithCustomerEmployeeResourceAfterDate(employeeids,
					resourceids, startdate, AppointmentStatus.PLANNED);
		}

		appointmentsToTest = appointmentService.cleanseAppointmentsOfBlocker(appointmentsToTest);

		appointmentsToTest.removeIf(app -> !isPullable(app.getAppointmentWithNewDatesFor(startdate)));

		return appointmentsToTest;
	}

	private void saveAppointmentgroupInternal(Appointmentgroup appointmentgroup) {
		List<Appointment> appointments = appointmentgroup.getAppointments();
		appointments.forEach(appointment -> appointmentRepository.save(appointment));
		appointmentgroupRepository.save(appointmentgroup);
	}

	private void testWarningsForAppointmentGroup(Appointmentgroup appointmentgroup) {
		testWarningsForAppointmentGroup(appointmentgroup, new OverrideBookingTester(new ArrayList<>()));
	}

	private void testWarningsForAppointmentGroup(Appointmentgroup appointmentgroup, BookingTester tester) {
		boolean shouldOverride = tester instanceof OverrideBookingTester;

		appointmentgroup.resetAllWarnings();

		appointmentgroup.testProcedureRelations(shouldOverride);

		appointmentgroup.testBookability(restrictionService, appointmentService, tester);
	}

	private Appointmentgroup getUpdatableAppointmentgroupContainingAppointmentID(String appointmentid) {
		Appointmentgroup appointmentgroup = getAppointmentgroupContainingAppointmentID(appointmentid);
		List<Appointment> appointments = appointmentgroup.getAppointments();
		boolean isUpdateble = appointmentgroup.getStatus().isActive();

		isUpdateble &= appointments.stream().noneMatch(app -> hasActualTimeValue(app) && app.getStatus().isDeleted());

		return isUpdateble ? appointmentgroup : null;
	}

	private void testUserdataInAppointmentWithOverride(Appointment appointment, User user, boolean noUserAttached,
			boolean override) {
		try {
			testUserdataInAppointment(appointment, user, noUserAttached);
		} catch (BookedCustomerException ex) {
			if (!override)
				throw ex;

			appointment.addWarning(Warning.USER_WARNING);
		}
	}

	private void testUserdataInAppointment(Appointment appointment, User user, boolean noUserAttached) {
		boolean isUserPresentInAppointment = appointment.getBookedCustomer() != null;
		boolean isUserdataCorrect;

		// attached user is wrong
		if (!noUserAttached && isUserPresentInAppointment) {
			isUserdataCorrect = !appointment.getBookedCustomer().getId().equals(user.getId())
					|| user.getSystemStatus().isDeleted();
			if (isUserdataCorrect)
				throw new IllegalArgumentException(
						"User in the appointment for the procedure " + appointment.getBookedProcedure().getId()
								+ " does not conform to the given user for all appointments");
		}

		appointment.setBookedCustomer(user);
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

	private String getStringRepresentation(Date date) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
		LocalDateTime ldt = date.toInstant().atZone(ZoneId.of("UTC")).toLocalDateTime();

		return formatter.format(ldt);
	}
	
	private String generateLoginLink(User user) {
		String link = user.generateLoginLink();
		userService.encodePassword(user);
		userRepository.save(user);
		
		return link;
	}

}

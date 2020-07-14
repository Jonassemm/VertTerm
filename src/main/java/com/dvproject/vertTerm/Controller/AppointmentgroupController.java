package com.dvproject.vertTerm.Controller;

import java.util.*;

import com.dvproject.vertTerm.Model.*;
import com.dvproject.vertTerm.Service.*;
import com.dvproject.vertTerm.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.dvproject.vertTerm.security.AuthorityTester;
import com.dvproject.vertTerm.util.*;

@RestController
@RequestMapping("/Appointmentgroups")
@ResponseBody
public class AppointmentgroupController {
	@Autowired
	private AppointmentgroupService appointmentgroupService;

	@Autowired
	private AppointmentService appointmentService;

	@Autowired
	private RestrictionService restrictionService;

	@Autowired
	EmployeeService employeeService;

	@Autowired
	UserService userService;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	ResourceService resourceService;

	@GetMapping("")
	public List<Appointmentgroup> getAllAppointmentgroups() {
		return appointmentgroupService.getAll();
	}

	@GetMapping("/Status/{status}")
	public List<Appointmentgroup> getAllAppointmentgroupsWithStatus(@PathVariable Status status) {
		return appointmentgroupService.getAppointmentgroupsWithStatus(status);
	}

	@GetMapping("/{id}")
	public Appointmentgroup getAppointmentGroup(@PathVariable String id) {
		return appointmentgroupService.getById(id);
	}

	@GetMapping("/Appointment/{id}")
	public Appointmentgroup getAppointmentGroupByAppointmentId(@PathVariable String id) {
		return appointmentgroupService.getAppointmentgroupContainingAppointmentID(id);
	}

	@PostMapping(value = { "/", "/{userid}" })
	public String bookAppointments(@PathVariable(required = false) String userid,
			@RequestBody Appointmentgroup appointmentgroup) {
		boolean customerAttached = userid != null && !userid.equals("");
		User bookedCustomer = customerAttached ? userService.getById(userid) : userService.getAnonymousUser();
		
		testCallersRights(bookedCustomer);
		
		appointmentgroupService.loadAppointmentgroup(appointmentgroup);
		appointmentgroup.changeBookedCustomer(bookedCustomer);

		new NormalBooker(appointmentgroup).bookable(appointmentService, restrictionService, userRepository);

		String login = !customerAttached ? generateLogin(bookedCustomer) : null;

		appointmentgroupService.saveAppointmentgroup(appointmentgroup);

		return login;
	}

	@PostMapping(value = { "/override/", "/override/{userid}" })
	public String bookAppointmentsOverride(
			@PathVariable String userid, 
			@RequestBody Appointmentgroup appointmentgroup) 
	{
		AuthorityTester.containsAny("OVERRIDE");

		boolean customerAttached = userid != null && !userid.equals("");
		User bookedCustomer = customerAttached ? userService.getById(userid) : userService.getAnonymousUser();
		
		testCallersRights(bookedCustomer);
		
		appointmentgroupService.loadAppointmentgroup(appointmentgroup);
		appointmentgroup.changeBookedCustomer(bookedCustomer);

		new OverrideBooker(appointmentgroup).bookable(appointmentService, restrictionService, userRepository);

		String login = customerAttached ? generateLogin(bookedCustomer) : null;

		appointmentgroupService.saveAppointmentgroup(appointmentgroup);

		return login;
	}

	@PutMapping("/{userid}")
	public void updateAppointments(
			@PathVariable String userid,
			@RequestBody Appointmentgroup appointmentgroup)
	{
		User user = userRepository.findById(userid).orElse(new User());
		testCallersRights(user);
		updateAppointmentgroup(userid, appointmentgroup, new NormalBooker(appointmentgroup));

	}

	@PutMapping("/override/{userid}")
	public void updateAppointmentsOverride(
			@PathVariable String userid, 
			@RequestBody Appointmentgroup appointmentgroup) 
	{
		AuthorityTester.containsAny("OVERRIDE");
		User user = userRepository.findById(userid).orElse(new User());
		testCallersRights(user);

		updateAppointmentgroup(userid, appointmentgroup, new OverrideBooker(appointmentgroup));
	}

	@PutMapping("/start/{appointmentId}")
	public boolean startAppointment(@PathVariable String appointmentId) {
		Appointment appointmentToStart = appointmentService.getById(appointmentId);
		return appointmentgroupService.startAppointment(appointmentToStart);
	}

	@PutMapping("/stop/{appointmentId}")
	public boolean stopAppointment(@PathVariable String appointmentId) {
		Appointment appointmentToStop = appointmentService.getById(appointmentId);
		boolean retVal = appointmentgroupService.stopAppointment(appointmentToStop);
		
		if (appointmentToStop.getActualEndtime().before(appointmentToStop.getPlannedEndtime()))
			appointmentgroupService.setPullableAppointments(appointmentToStop);
		
		return retVal;
	}

	@PutMapping("/test/{appointmentid}")
	public List<Warning> testWarningsofAppointment(@PathVariable String appointmentid) {
		Appointmentgroup appointmentgroup = appointmentgroupService
				.getAppointmentgroupContainingAppointmentID(appointmentid);

		appointmentgroup.resetAllWarnings();

		new OverrideBooker(appointmentgroup).bookable(appointmentService, restrictionService, userRepository);
		appointmentgroupService.saveAppointmentgroup(appointmentgroup);

		return appointmentService.getById(appointmentid).getWarnings();
	}

	@DeleteMapping("/{id}")
	public boolean deleteAppointmentGroup(@PathVariable(name = "id") String appointmentGroupId) {
		return appointmentgroupService.delete(appointmentGroupId);
	}

	@DeleteMapping("/Appointment/{id}")
	public boolean deleteAppointment(@PathVariable(name = "id") String appointmentId) {
		Appointment appointment = appointmentService.getById(appointmentId);
		
		String userid = appointment.getBookedCustomer().getId();
		User user = userRepository.findById(userid).orElse(new User());
		testCallersRights(user);
		
		boolean retVal = appointmentgroupService.deleteAppointment(appointment, new NormalBooker());
		
		testWarningsForAppointmentsInTimeInterval(appointment);
		
		return retVal;
	}

	@DeleteMapping("/override/Appointment/{id}")
	public boolean deleteAppointmentOverride(@PathVariable(name = "id") String appointmentId) {
		Appointment appointment = appointmentService.getById(appointmentId);
		
		AuthorityTester.containsAny("OVERRIDE");
		String userid = appointment.getBookedCustomer().getId();
		User user = userRepository.findById(userid).orElse(new User());
		testCallersRights(user);
		
		boolean retVal = appointmentgroupService.deleteAppointment(appointment, new OverrideBooker());
		
		testWarningsForAppointmentsInTimeInterval(appointment);
		
		return retVal;
	}

	private void updateAppointmentgroup(String userid, Appointmentgroup appointmentgroup, Booker booker) {
		Appointmentgroup appointmentgroupFromDB = null;
		String appointmentgroupid = appointmentgroup.getId();
		
		if (!appointmentgroup.hasAllAppointmentidsSet())
			throw new IllegalArgumentException("Appointments must contain ids");
		
		if (appointmentgroupid == null) {
			Appointment appointmentOfAppointmentgroup = appointmentgroup.getAppointments().get(0);
			appointmentgroupFromDB = appointmentgroupService.getAppointmentgroupContainingAppointmentID(appointmentOfAppointmentgroup.getId());
			appointmentgroup.setId(appointmentgroupFromDB.getId());
		}

		appointmentgroupService.loadAppointmentgroup(appointmentgroup);

		appointmentgroup.resetAllWarnings();
		
		booker.bookable(appointmentService, restrictionService, userRepository);

		appointmentgroupService.saveAppointmentgroup(appointmentgroup);
		
		testWarningsForAppointmentsInTimeInterval(appointmentgroupFromDB);
	}

	@PostMapping("/Recommend/EarlyEnd/{index}")
	public @ResponseBody Appointmentgroup recommendByEarlyEnd(@RequestBody Appointmentgroup appointments,
			@PathVariable int index) {
		PopulateCustomer(appointments);
		for (Appointment appointment : appointments.getAppointments()) {
			appointment.setStatus(AppointmentStatus.OPEN);
		}
		appointments.optimizeAppointmentsForEarliestEnd(appointmentService, resourceService, employeeService,
				appointments.getAppointments().get(index));
		return appointments;
	}

	@PostMapping("/Recommend/LeastWaitingTime/{index}")
	public @ResponseBody Appointmentgroup recommendByLeastWaitingTime(@RequestBody Appointmentgroup appointments,
			@PathVariable int index) {
		PopulateCustomer(appointments);
		appointments.optimizeForLeastWaitingTime(appointmentService, resourceService, employeeService,
				appointments.getAppointments().get(index));
		return appointments;
	}

	@PostMapping("/Recommend/LeastDays/{index}")
	public @ResponseBody Appointmentgroup recommendByLeastDays(@RequestBody Appointmentgroup appointments,
			@PathVariable int index) {
		PopulateCustomer(appointments);
		appointments.optimizeForLeastDays(appointmentService, resourceService, employeeService,
				appointments.getAppointments().get(index));
		return appointments;
	}

	@PostMapping("/Recommend/LateBeginning/{index}")
	public @ResponseBody Appointmentgroup recommendByLateBeginning(@RequestBody Appointmentgroup appointments,
			@PathVariable int index) {
		for (Appointment appointment : appointments.getAppointments()) {
			appointment.setStatus(AppointmentStatus.OPEN);
		}
		PopulateCustomer(appointments);
		appointments.optimizeAppointmentsForLatestBeginning(appointmentService, resourceService, employeeService,
				appointments.getAppointments().get(index));
		return appointments;
	}

	private void PopulateCustomer(Appointmentgroup appointmentgroup) {
		Appointment firstAppointment = appointmentgroup.getAppointments().get(0);
		User user = FindUserForAppointments(firstAppointment.getBookedCustomer());
		for (Appointment appointment : appointmentgroup.getAppointments()) {
			appointment.setBookedCustomer(user);
		}
	}

	private User FindUserForAppointments(User user) {
		if (user == null) {
			return userService.getAnonymousUser();
		} else
			return userService.getById(user.getId());
	}

	private String generateLogin(User user) {
		String link = user.generateLoginLink();
		userService.encodePassword(user);
		userRepository.save(user);

		return link;
	}
	
	private void testWarningsForAppointmentsInTimeInterval(Appointmentgroup appointmentgroupToTestAppointments) {
		List<Appointment> appointmentsToTest = new ArrayList<>();
		
		appointmentgroupToTestAppointments.getAppointments().forEach(app -> {
			List<Appointment> appointmentsInTimeinterval = appointmentService
					.getAppointmentsInTimeIntervalAndStatus(app.getPlannedStarttime(), app.getPlannedEndtime(), AppointmentStatus.PLANNED);
			appointmentsToTest.addAll(appointmentsInTimeinterval);
		});
		
		appointmentgroupService.testWarningsForAppointments(appointmentsToTest);
	}
	
	
	private void testWarningsForAppointmentsInTimeInterval(Appointment appointment) {
		List<Appointment> appointmentsToTest = appointmentService.getOverlappingAppointmentsInTimeInterval(
				appointment.getPlannedStarttime(), appointment.getPlannedEndtime(), AppointmentStatus.PLANNED);

		appointmentgroupService.testWarningsForAppointments(appointmentsToTest);

		appointmentgroupService.setPullableAppointments(appointment);
	}
	
	private void testCallersRights(User userToTest) {
		if (AuthorityTester.isLoggedInUser(userToTest)) {
			AuthorityTester.containsAny("OWN_APPOINTMENT_WRITE");
		} else {
			AuthorityTester.containsAny("APPOINTMENT_WRITE");
		}
	}
}

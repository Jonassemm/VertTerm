package com.dvproject.vertTerm.Controller;

import com.dvproject.vertTerm.Model.*;
import com.dvproject.vertTerm.Service.*;
import com.dvproject.vertTerm.repository.UserRepository;
import com.dvproject.vertTerm.security.AuthorityTester;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.*;

@RestController
@RequestMapping("/Appointments")
public class AppointmentController {
	@Autowired
	AppointmentService service;

	@Autowired
	EmployeeService employeeService;

	@Autowired
	ResourceService resourceService;

	@Autowired
	private AppointmentgroupService appointmentgroupService;

	@Autowired
	private UserService userService;
	
	@Autowired
	private UserRepository userRepository;

	/**
	 * @author Robert Schulz
	 */
	@GetMapping()
	public @ResponseBody List<Appointment> get() {
		return service.getAll();
	}

	/**
	 * @author Robert Schulz
	 */
	@GetMapping("/{id}")
	public @ResponseBody Appointment get(@PathVariable String id) {
		return service.getById(id);
	}

	/**
	 * @author Robert Schulz
	 */
	@PostMapping("/Recommend/EarlyEnd")
	public @ResponseBody Appointment recommendByEarlyEnd(@RequestBody Appointment appointment) {
		appointment.optimizeAndPopulateForEarliestEnd(service, resourceService, employeeService);
		return appointment;
	}

	/**
	 * @author Robert Schulz
	 */
	@PostMapping("/Recommend/LateBeginning")
	public @ResponseBody Appointment recommendByLateBeginning(@RequestBody Appointment appointment) {
		appointment.optimizeAndPopulateForLatestBeginning(service, resourceService, employeeService);
		return appointment;
	}

	/**
	 * @author Joshua Müller
	 */
	@GetMapping("/pull/{id}")
	public void testPullability(@PathVariable String id) {
		Appointment appointment = service.getById(id);
		appointmentgroupService.setPullableAppointment(appointment);
	}

	/**
	 * @author Joshua Müller
	 */
	@GetMapping("/Own")
	public @ResponseBody List<Appointment> getOwnAppointments(Principal principal) {
		AuthorityTester.containsAny("OWN_APPOINTMENT_READ");
		if (principal == null) { throw new IllegalArgumentException("No principal available"); }

		User user = userService.getOwnUser(principal);
		String id = user.getId();
		List<Appointment> appointments = service.getAppointmentsByUserIdAndAppointmentStatus(id, null);
		if (user instanceof Employee)
			appointments.addAll(service.getAppointmentsByEmployeeIdAndAppointmentStatus(id, null));

		return appointments;
	}

	/**
	 * @author Joshua Müller
	 */
	@GetMapping("/Resources/{resourceId}")
	public @ResponseBody List<Appointment> getByResourceId(
			@PathVariable String resourceId,
			@RequestParam(required = false) Date starttime,
			@RequestParam(required = false) Date endtime,
			@RequestParam(required = false, name = "status") String statusString) 
	{
		AuthorityTester.containsAny("APPOINTMENT_READ");
		List<Appointment> appointments = null;
		AppointmentStatus status = AppointmentStatus.enumOf(statusString);

		if (starttime == null || endtime == null) {
			appointments = service.getAppointmentsByResourceIdAndAppointmentStatus(resourceId, status);
		} else
			if (starttime != null && endtime != null) {
				appointments = service.getAppointmentsOfBookedResourceInTimeinterval(resourceId, starttime, endtime,
						status);
			}

		return appointments;
	}

	/**
	 * @author Joshua Müller
	 */
	@GetMapping("/user/{userid}")
	public List<Appointment> getAppointmentsWithUserInTimeInterval(
			@PathVariable String userid,
			@RequestParam(required = false) Date starttime, 
			@RequestParam(required = false) Date endtime,
			@RequestParam(required = false, name = "status") String statusString) 
	{
		List<Appointment> appointments = new ArrayList<>();
		User user = userRepository.findById(userid).orElseThrow();
		boolean isEmployee = user instanceof Employee;
		AppointmentStatus appointmentStatus = AppointmentStatus.enumOf(statusString);
		
		testCallersAppointmentReadRight(user);

		if (starttime == null || endtime == null) {
			appointments.addAll(service.getAppointmentsByUserIdAndAppointmentStatus(userid, appointmentStatus));

			if (isEmployee)
				appointments.addAll(service.getAppointmentsByEmployeeIdAndAppointmentStatus(userid, appointmentStatus));
		} else
			if (starttime != null && endtime != null) {
				appointments.addAll(
						service.getAppointmentsOfBookedCustomerInTimeinterval(userid, starttime, endtime, appointmentStatus));

				if (isEmployee)
					appointments.addAll(service.getAppointmentsOfBookedEmployeeInTimeinterval(userid, starttime, endtime,
							appointmentStatus));
			}

		return appointments;
	}

	/**
	 * @author Joshua Müller
	 */
	@GetMapping("/warnings")
	public List<Appointment> getAppointmentsWithWarnings(
			@RequestParam(required = false) String userid,
			@RequestParam(required = false, name = "warnings") List<String> warningStrings) 
	{
		List<Warning> warnings = null;
		User user = userRepository.findById(userid).orElseThrow();
		boolean areWarningStringsEmpty = warningStrings == null || warningStrings.isEmpty();
		
		testCallersAppointmentReadRight(user);

		warnings = areWarningStringsEmpty ? Warning.getAll() : Warning.enumOf(warningStrings);

		return service.getAllAppointmentsByUseridAndWarnings(userid, warnings);
	}

	/**
	 * @author Joshua Müller
	 */
	@GetMapping({ "/status/{status}", "/status/" })
	public List<Appointment> getAppointmentsInTimeInterval(
			@PathVariable(required = false) AppointmentStatus status,
			@RequestParam Date starttime, 
			@RequestParam Date endtime) 
	{
		return service.getAppointmentsInTimeIntervalAndStatus(starttime, endtime, status);
	}

	@PostMapping("/ResEmp")
	public @ResponseBody Appointmentgroup getAvailableResourcesAndEmployees(@RequestBody Appointmentgroup group) {
		return service.getAvailableResourcesAndEmployees(group);
	}

	/**
	 * @author Robert Schulz
	 */
	@PostMapping()
	public @ResponseBody Appointment create(@RequestBody Appointment newAppointment) {
		return service.create(newAppointment);
	}

	/**
	 * @author Robert Schulz
	 */
	@PutMapping("/{id}")
	public Appointment UpdateAppointment(@RequestBody Appointment newAppointment) {
		return service.update(newAppointment);
	}

	/**
	 * @author Robert Schulz
	 */
	@PutMapping("/{id}/{customerIsWaiting}")
	public boolean setCustomerIsWaiting(@PathVariable String id, @PathVariable boolean customerIsWaiting) {
		return service.setCustomerIsWaiting(id, customerIsWaiting);
	}

	/**
	 * @author Robert Schulz
	 */
	@DeleteMapping("/{id}")
	public boolean DeleteAppointment(@PathVariable String id) {
		return service.delete(id);
	}
	
	/**
	 * @author Joshua Müller
	 */
	private void testCallersAppointmentReadRight(User userToTest) {
		if (AuthorityTester.isLoggedInUser(userToTest)) {
			try {
				AuthorityTester.containsAny("OWN_APPOINTMENT_READ");
			} catch (RuntimeException ex) {
				AuthorityTester.containsAny("APPOINTMENT_READ");
			}
		} else {
			AuthorityTester.containsAny("APPOINTMENT_READ");
		}
	}
}

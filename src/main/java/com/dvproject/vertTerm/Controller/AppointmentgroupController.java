package com.dvproject.vertTerm.Controller;

import java.security.Principal;
import java.util.*;

import com.dvproject.vertTerm.Model.*;
import com.dvproject.vertTerm.Service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.dvproject.vertTerm.Service.AppointmentService;
import com.dvproject.vertTerm.Service.AppointmentgroupService;
import com.dvproject.vertTerm.security.AuthorityTester;

@RestController
@RequestMapping("/Appointmentgroups")
@ResponseBody
public class AppointmentgroupController {
	@Autowired
	private AppointmentgroupService appointmentgroupService;

	@Autowired
	private AppointmentService appointmentService;

	@Autowired
	EmployeeService employeeService;

	@Autowired
	UserService userService;

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

	@PostMapping(value = {"/", "/{userid}"})
	public String bookAppointments (
			@PathVariable(required = false) String userid, 
			@RequestBody Appointmentgroup appointmentgroup,
			Principal principal) {
		return this.bookAppointmentgroup(userid, appointmentgroup, principal, false);
	}

	@PostMapping(value = { "/override/", "/override/{userid}" })
	public String bookAppointmentsOverride(@PathVariable String userid, @RequestBody Appointmentgroup appointmentgroup,
			Principal principal) {
		AuthorityTester.containsAny("OVERRIDE");
		return this.bookAppointmentgroup(userid, appointmentgroup, principal, true);
	}

	@PutMapping("/{userid}")
	public String updateAppointments(@PathVariable String userid, @RequestBody Appointmentgroup appointmentgroup,
			Principal principal) {
		return updateAppointmentgroupInternal(principal, userid, appointmentgroup, false);
	}

	@PutMapping("/override/{userid}")
	public String updateAppointmentsOverride(@PathVariable String userid,
			@RequestBody Appointmentgroup appointmentgroup, Principal principal) {
		AuthorityTester.containsAny("OVERRIDE");
		return updateAppointmentgroupInternal(principal, userid, appointmentgroup, true);
	}

	@PutMapping("/start/{appointmentId}")
	public boolean startAppointment(@PathVariable(required = true) String appointmentId) {
		return appointmentgroupService.startAppointment(appointmentId);
	}

	@PutMapping("/stop/{appointmentId}")
	public boolean stopAppointment(@PathVariable(required = true) String appointmentId) {
		return appointmentgroupService.stopAppointment(appointmentId);
	}

	@PutMapping("/test/{appointmentid}")
	public List<Warning> testWarningsofAppointment(@PathVariable String appointmentid) {
		appointmentgroupService.testWarnings(appointmentid);
		return appointmentService.getById(appointmentid).getWarnings();
	}

	@DeleteMapping("/{id}")
	public boolean deleteAppointmentGroup(@PathVariable(name = "id") String appointmentGroupId) {
		return appointmentgroupService.delete(appointmentGroupId);
	}

	@DeleteMapping("/Appointment/{id}")
	public boolean deleteAppointment(@PathVariable(name = "id") String appointmentId) {
		return appointmentgroupService.deleteAppointment(appointmentId, false);
	}

	@DeleteMapping("/override/Appointment/{id}")
	public boolean deleteAppointmentOverride(@PathVariable(name = "id") String appointmentId) {
		AuthorityTester.containsAny("OVERRIDE");
		return appointmentgroupService.deleteAppointment(appointmentId, true);
	}

	private String updateAppointmentgroupInternal(Principal principal, String userid, Appointmentgroup appointmentgroup,
			boolean override) {
		List<Appointment> appointments = appointmentgroup.getAppointments();

		if (appointments == null || appointments.size() == 0)
			throw new IllegalArgumentException("Appointmentgroup must contain at least one appointment");

		if (!appointmentgroup.hasAllAppointmentIdSet())
			throw new IllegalArgumentException("Appointments must contain ids");

		appointmentgroupService.canBookAppointments(principal, appointmentgroup);

		appointmentgroup.setId(appointmentgroupService
				.getAppointmentgroupContainingAppointmentID(appointments.get(0).getId()).getId());

		appointmentgroup.resetAllWarnings();

		return appointmentgroupService.bookAppointmentgroup(userid, appointmentgroup, override);
	}

	private String bookAppointmentgroup(String userid, Appointmentgroup appointmentgroup, Principal principal,
			boolean override) {
		if (!appointmentgroup.hasNoAppointmentIdSet())
			throw new IllegalArgumentException("Appointments must not contain ids");

		appointmentgroupService.canBookAppointments(principal, appointmentgroup);

		return appointmentgroupService.bookAppointmentgroup(userid, appointmentgroup, override);
	}

	/**
	 * @author Robert Schulz
	 */
	@PostMapping("/Recommend/EarlyEnd/{index}")
	public @ResponseBody Appointmentgroup recommendByEarlyEnd(@RequestBody Appointmentgroup appointments, @PathVariable int index) {
		PopulateCustomer(appointments);
		for (Appointment appointment : appointments.getAppointments()){
			appointment.setStatus(AppointmentStatus.OPEN);
		}
		appointments.optimizeAppointmentsForEarliestEnd(appointmentService, resourceService, employeeService, appointments.getAppointments().get(index));
		return appointments;
	}

	/**
	 * @author Robert Schulz
	 */
	@PostMapping("/Recommend/LeastWaitingTime/{index}")
	public @ResponseBody Appointmentgroup recommendByLeastWaitingTime(@RequestBody Appointmentgroup appointments, @PathVariable int index) {
		PopulateCustomer(appointments);
		appointments.optimizeForLeastWaitingTime(appointmentService, resourceService, employeeService, appointments.getAppointments().get(index));
		return appointments;
	}

	/**
	 * @author Robert Schulz
	 */
	@PostMapping("/Recommend/LeastDays/{index}")
	public @ResponseBody Appointmentgroup recommendByLeastDays(@RequestBody Appointmentgroup appointments, @PathVariable int index) {
		PopulateCustomer(appointments);
		appointments.optimizeForLeastDays(appointmentService, resourceService, employeeService, appointments.getAppointments().get(index));
		return appointments;
	}

	/**
	 * @author Robert Schulz
	 */
	@PostMapping("/Recommend/LateBeginning/{index}")
	public @ResponseBody Appointmentgroup recommendByLateBeginning(@RequestBody Appointmentgroup appointments,
			@PathVariable int index) {
		for (Appointment appointment : appointments.getAppointments()) {
			appointment.setStatus(AppointmentStatus.OPEN);
		}
		PopulateCustomer(appointments);
		appointments.optimizeAppointmentsForLatestBeginning(appointmentService, resourceService, employeeService, appointments.getAppointments().get(index));
		return appointments;
	}

	/**
	 * @author Robert Schulz
	 */
	private void PopulateCustomer(Appointmentgroup appointmentgroup){
		Appointment firstAppointment = appointmentgroup.getAppointments().get(0);
		User user = FindUserForAppointments(firstAppointment.getBookedCustomer());
		for(Appointment appointment : appointmentgroup.getAppointments()){
			appointment.setBookedCustomer(user);
		}
	}

	/**
	 * @author Robert Schulz
	 */
	private User FindUserForAppointments(User user){
		if(user == null){
			return userService.getAnonymousUser();
		}
		else return userService.getById(user.getId());
	}
}

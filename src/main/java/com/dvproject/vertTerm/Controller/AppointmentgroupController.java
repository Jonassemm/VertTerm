package com.dvproject.vertTerm.Controller;

import java.security.Principal;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.dvproject.vertTerm.Model.Appointment;
import com.dvproject.vertTerm.Model.Appointmentgroup;
import com.dvproject.vertTerm.Model.Optimizationstrategy;
import com.dvproject.vertTerm.Model.Status;
import com.dvproject.vertTerm.Service.AppointmentgroupService;

@RestController
@RequestMapping("/Appointmentgroups")
@ResponseBody
public class AppointmentgroupController {
	@Autowired
	private AppointmentgroupService appointmentgroupService;
	
	@GetMapping("")
	public List<Appointmentgroup> getAllAppointmentgroups () {
		return appointmentgroupService.getAll();
	}

	@GetMapping("/Status/{status}")
	public List<Appointmentgroup> getAllAppointmentgroupsWithStatus (@PathVariable Status status) {
		return appointmentgroupService.getAppointmentgroupsWithStatus(status);
	}

	@GetMapping("/{id}")
	public Appointmentgroup getAppointmentGroup (@PathVariable String id) {
		return appointmentgroupService.getById(id);
	}
	
	@GetMapping("/appointment/{id}")
	public Appointmentgroup getAppointmentGroupByAppointmentId (@PathVariable String id) {
		return appointmentgroupService.getAppointmentgroupContainingAppointmentID(id);
	}
	
	@GetMapping("/Optimize")
	public Appointmentgroup getOptimizedSuggestion (
			@RequestBody Appointmentgroup appointmentgroup,
			@RequestParam Optimizationstrategy optimizationstrategy) {
		//TODO
		return appointmentgroupService.getOptimizedSuggestion(appointmentgroup, optimizationstrategy);
	}
	
	@PostMapping(value = {"/", "/{userid}"})
	public String bookAppointments (
			@PathVariable(required = false) String userid, 
			@RequestBody Appointmentgroup appointmentgroup,
			Principal principal) {
		return this.bookAppointmentgroup(userid, appointmentgroup, principal, false);
	}
	
	@PostMapping(value = {"/override/", "/override/{userid}"})
	public String bookAppointmentsOverride (
			@PathVariable String userid, 
			@RequestBody Appointmentgroup appointmentgroup,
			Principal principal) {
		Collection<? extends GrantedAuthority> auth = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
		
		return this.bookAppointmentgroup(userid, appointmentgroup, principal, true);
	}
	
	@PutMapping("")
	public Appointmentgroup updateAppointmentgroup (@RequestBody Appointmentgroup appointmentgroup) {
		return appointmentgroupService.update(appointmentgroup);
	}
	
	@PutMapping("/{userid}")
	public String updateAppointments (
			@PathVariable String userid, 
			@RequestBody Appointmentgroup appointmentgroup,
			Principal principal) {
		return updateAppointmentgroupInternal(principal, userid, appointmentgroup, false);
	}
	
	@PutMapping("/override{userid}")
	public String updateAppointmentsOverride (
			@PathVariable String userid, 
			@RequestBody Appointmentgroup appointmentgroup,
			Principal principal) {
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
	public void testWarningsofAppointment (@PathVariable String appointmentid) {
		appointmentgroupService.testWarnings(appointmentid);
	}
	
	@DeleteMapping("/{id}")
	public boolean deleteAppointmentGroup (@PathVariable(name = "id")String appointmentGroupId) {
		return appointmentgroupService.delete(appointmentGroupId);
	}
	
	@DeleteMapping("/appointment/{id}")
	public boolean deleteAppointment (@PathVariable(name = "id") String appointmentId) {
		return appointmentgroupService.deleteAppointment(appointmentId, false);
	}
	
	@DeleteMapping("/override/appointment/{id}")
	public boolean deleteAppointmentOverride (@PathVariable(name = "id") String appointmentId) {
		return appointmentgroupService.deleteAppointment(appointmentId, true);
	}
	
	private String updateAppointmentgroupInternal(Principal principal, String userid, Appointmentgroup appointmentgroup, boolean override) {
		List<Appointment> appointments = appointmentgroup.getAppointments();
		
		if(appointments == null || appointments.size() == 0)
			throw new IllegalArgumentException("Appointmentgroup must contain at least one appointment");
		
		if(!appointmentgroup.hasAllAppointmentIdSet())
			throw new IllegalArgumentException("Appointments must contain ids");
		
		appointmentgroupService.canBookAppointments(principal, appointmentgroup);
		
		appointmentgroup.setId(appointmentgroupService.getAppointmentgroupContainingAppointmentID(appointments.get(0).getId()).getId());
		
		return appointmentgroupService.bookAppointmentgroup(userid, appointmentgroup, override);
	}
	
	private String bookAppointmentgroup(String userid, Appointmentgroup appointmentgroup, Principal principal, boolean override) {
		if(!appointmentgroup.hasNoAppointmentIdSet())
			throw new IllegalArgumentException("Appointments must not contain ids");
		
		appointmentgroupService.canBookAppointments(principal, appointmentgroup);
		
		return appointmentgroupService.bookAppointmentgroup(userid, appointmentgroup, override);
	}
}

package com.dvproject.vertTerm.Controller;

import java.util.Collection;
import java.util.Date;
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
import com.dvproject.vertTerm.Model.User;
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
	
	@GetMapping("/Status")
	public List<Appointmentgroup> getAllAppointmentgroupsWithStatus (@RequestBody Status status){
		return appointmentgroupService.getAppointmentgroupsWithStatus(status);
	}
	
	@GetMapping("/{id}")
	public Appointmentgroup getAppointmentGroup (@PathVariable String id) {
		return appointmentgroupService.getById(id);
	}
	
	@GetMapping("/Appointment/{id}")
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
	
	@GetMapping("/Shift/{appointmentId}")
	public Appointment shiftAppointment (
			@PathVariable String appointmentId,
			@RequestParam Date startdate,
			@RequestParam Date enddate) {
		return appointmentgroupService.shiftAppointment(appointmentId, startdate, enddate);
	}
	
	@PostMapping("/{userid}")
	public User bookAppointments (
			@PathVariable String userid, 
			@RequestBody Appointmentgroup appointmentgroup) {
		return appointmentgroupService.bookAppointmentgroup(userid, appointmentgroup, false);
	}
	
	@PostMapping("/override/{userid}")
	public User bookAppointmentsOverride (
			@PathVariable String userid, 
			@RequestBody Appointmentgroup appointmentgroup) {
		Collection<? extends GrantedAuthority> auth = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
		
		return appointmentgroupService.bookAppointmentgroup(userid, appointmentgroup, true);
	}
	
	@PutMapping("")
	public Appointmentgroup updateAppointmentgroup (@RequestBody Appointmentgroup appointmentgroup) {
		return appointmentgroupService.update(appointmentgroup);
	}
	
	@PutMapping("/start/{appointmentId}")
	public boolean startAppointment(@PathVariable(required = true) String appointmentId) {
		return appointmentgroupService.startAppointment(appointmentId);
	}
	
	@PutMapping("/stop/{appointmentId}")
	public boolean stopAppointment(@PathVariable(required = true) String appointmentId) {
		return appointmentgroupService.stopAppointment(appointmentId);
	}
	
	@DeleteMapping("/{id}")
	public boolean deleteAppointmentGroup (@PathVariable String id) {
		return appointmentgroupService.delete(id);
	}
}

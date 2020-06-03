package com.dvproject.vertTerm.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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
		return appointmentgroupService.getAppointmentgroupWithAppointmentID(id);
	}
	
	@GetMapping("/Optimize")
	public Appointmentgroup getOptimizedSuggestion (
			@RequestBody Appointmentgroup appointmentgroup,
			@RequestParam Optimizationstrategy optimizationstrategy) {
		//TODO
		return appointmentgroupService.getOptimizedSuggestion(appointmentgroup, optimizationstrategy);
	}
	
	@PostMapping("")
	public boolean bookAppointments (
			@RequestParam(defaultValue = "", required = false) String userid, 
			@RequestBody Appointmentgroup appointmentgroup) {
		return appointmentgroupService.bookAppointmentgroup(userid, appointmentgroup);
	}
	
	@PutMapping("")
	public Appointmentgroup updateAppointmentgroup (@RequestBody Appointmentgroup appointmentgroup) {
		return appointmentgroupService.update(appointmentgroup);
	}
	
	@DeleteMapping("/{id}")
	public boolean deleteAppointmentGroup (@PathVariable String id) {
		return appointmentgroupService.delete(id);
	}
}

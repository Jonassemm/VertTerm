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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.dvproject.vertTerm.Model.Appointmentgroup;
import com.dvproject.vertTerm.Service.AppointmentgroupService;

@RestController
@RequestMapping("/Appointmentgroups")
@ResponseBody
public class AppointmentgroupController {
	@Autowired
	private AppointmentgroupService appointmentgroupService;
	
	@GetMapping("")
	private List<Appointmentgroup> getAllAppointmentGroups () {
		return appointmentgroupService.getAll();
	}
	
	@GetMapping("/{id}")
	private Appointmentgroup getAppointmentGroup (@PathVariable String id) {
		return appointmentgroupService.getById(id);
	}
	
	@GetMapping("/Appointment/{id}")
	private Appointmentgroup getAppointmentGroupByAppointmentId (@PathVariable String id) {
		return appointmentgroupService.getAppointmentgroupWithAppointmentID(id);
	}
	
	@PostMapping("")
	private Appointmentgroup insertAppointmentgroup (@RequestBody Appointmentgroup appointmentgroup) {
		return appointmentgroupService.create(appointmentgroup);
	}
	
	@PutMapping("")
	private Appointmentgroup updateAppointmentgroup (@RequestBody Appointmentgroup appointmentgroup) {
		return appointmentgroupService.update(appointmentgroup);
	}
	
	@DeleteMapping("/{id}")
	private boolean deleteAppointmentGroup (@PathVariable String id) {
		return appointmentgroupService.delete(id);
	}
}

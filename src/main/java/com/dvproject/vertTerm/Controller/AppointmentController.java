package com.dvproject.vertTerm.Controller;

import com.dvproject.vertTerm.Model.Appointment;
import com.dvproject.vertTerm.Model.AppointmentStatus;
import com.dvproject.vertTerm.Model.Warning;
import com.dvproject.vertTerm.Service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/Appointments")
public class AppointmentController {
    @Autowired
    AppointmentService service;


    @GetMapping()
    public @ResponseBody
    List<Appointment> get()
    {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public @ResponseBody
    Appointment get(@PathVariable String id)
    {
        return service.getById(id);
    }
    
    @GetMapping("/user/{userid}")
    public List<Appointment> getAppointmentsWithUserInTimeInterval(
    		@PathVariable String userid,
    		@RequestParam(required = false) Date starttime,
    		@RequestParam(required = false) Date endtime){
    	List<Appointment> appointments = null;
    	
    	if (starttime == null && endtime == null) {
    		appointments = service.getAppointmentsByUserid(userid);
    	} else if (starttime != null && endtime != null) {
    			appointments =  service.getAppointmentsWithUseridAndTimeInterval(userid, starttime, endtime);
    	}
    	
    	return appointments;
    }
    
    @GetMapping(path = {"/Warnings/{userid}", "/Warnings", "/Warnings/"})
    public List<Appointment> getAppointmentsWithWarnings(
    		@PathVariable(required = false) String userid,
    		@RequestBody(required = true) List<Warning> warnings){
    	List<Appointment> appointments = null;
    	
    	if (userid == null || userid.equals("")) {
    		appointments = service.getAppointmentsByWarnings(warnings);
    	} else {
    		appointments = service.getAppointmentsByWarningsAndId(userid, warnings);
    	}
    	
    	return appointments;
    }
    
    @GetMapping("/status/{status}")
    public List<Appointment> getAppointmentsInTimeInterval(
    		@PathVariable AppointmentStatus status,
    		@RequestParam Date starttime,
    		@RequestParam Date endtime){
    	List<Appointment> retVal = null;
    	
    	if (status == null) {
    		retVal = service.getAppointmentsInTimeInterval(starttime, endtime);
    	} else {
    		retVal = service.getAppointmentsInTimeIntervalWithStatus(starttime, endtime, status);
    	}
    	
    	return retVal;
    }

    @PostMapping()
    public @ResponseBody
    Appointment create(@RequestBody Appointment newAppointment)
    {
        return service.create(newAppointment);
    }

    @PutMapping("/{id}")
    public Appointment UpdateAppointment(@RequestBody Appointment newAppointment)
    {
        return service.update(newAppointment);
    }
    
    @PutMapping("/{id}/{customerIsWaiting}")
    private boolean setCustomerIsWaiting(
    		@PathVariable String id,
    		@PathVariable boolean customerIsWaiting) {
    	return service.setCustomerIsWaiting(id, customerIsWaiting);
    }

    @DeleteMapping("/{id}")
    public boolean DeleteAppointment(@PathVariable String id)
    {
        return service.delete(id);
    }
}

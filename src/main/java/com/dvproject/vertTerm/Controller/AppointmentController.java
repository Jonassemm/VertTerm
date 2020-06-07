package com.dvproject.vertTerm.Controller;

import com.dvproject.vertTerm.Model.Appointment;
import com.dvproject.vertTerm.Service.AppointmentServiceImpl;
import com.dvproject.vertTerm.Service.BasicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/Appointments")
public class AppointmentController {
    @Autowired
    BasicService<Appointment> service;
    
    @Autowired
    private AppointmentServiceImpl appointmentService;


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
    		@RequestParam Date starttime,
    		@RequestParam Date endtime){
    	return appointmentService.getAppointmentsWithUseridAndTimeInterval(userid, starttime, endtime);
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

    @DeleteMapping("/{id}")
    public boolean DeleteAppointment(@PathVariable String id)
    {
        return service.delete(id);
    }
}

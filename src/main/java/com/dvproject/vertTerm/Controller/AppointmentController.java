package com.dvproject.vertTerm.Controller;

import com.dvproject.vertTerm.Model.Appointment;
import com.dvproject.vertTerm.Service.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/Appointments")
public class AppointmentController {
    @Autowired
    Service<Appointment> service;


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

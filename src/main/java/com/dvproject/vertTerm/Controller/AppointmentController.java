package com.dvproject.vertTerm.Controller;

import com.dvproject.vertTerm.Model.Appointment;
import com.dvproject.vertTerm.Service.BasicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/Appointments")
public class AppointmentController {
    @Autowired
    BasicService<Appointment> basicService;


    @GetMapping()
    public @ResponseBody
    List<Appointment> get()
    {
        return basicService.getAll();
    }

    @GetMapping("/{id}")
    public @ResponseBody
    Appointment get(@PathVariable String id)
    {
        return basicService.getById(id);
    }

    @PostMapping()
    public @ResponseBody
    Appointment create(@RequestBody Appointment newAppointment)
    {
        return basicService.create(newAppointment);
    }

    @PutMapping("/{id}")
    public Appointment UpdateAppointment(@RequestBody Appointment newAppointment)
    {
        return basicService.update(newAppointment);
    }

    @DeleteMapping("/{id}")
    public boolean DeleteAppointment(@PathVariable String id)
    {
        return basicService.delete(id);
    }
}

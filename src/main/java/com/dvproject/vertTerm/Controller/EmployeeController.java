package com.dvproject.vertTerm.Controller;

import com.dvproject.vertTerm.Model.Employee;
import com.dvproject.vertTerm.Service.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/Employees")
public class EmployeeController {
    @Autowired
    Service<Employee> service;


    @GetMapping()
    public @ResponseBody
    List<Employee> get()
    {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public @ResponseBody
    Employee get(@PathVariable String id)
    {
        return service.getById(id);
    }

    @PostMapping()
    public @ResponseBody
    Employee create(@RequestBody Employee newEmployee)
    {
        return service.create(newEmployee);
    }

    @PutMapping("/{id}")
    public Employee UpdateEmployee(@RequestBody Employee newEmployee)
    {
        return service.update(newEmployee);
    }

    @DeleteMapping("/{id}")
    public boolean DeleteEmployee(@PathVariable String id)
    {
        return service.delete(id);
    }
}

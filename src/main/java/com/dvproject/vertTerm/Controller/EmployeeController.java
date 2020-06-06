package com.dvproject.vertTerm.Controller;

import com.dvproject.vertTerm.Model.Employee;
import com.dvproject.vertTerm.Model.Status;
import com.dvproject.vertTerm.Service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/Employees")
public class EmployeeController {
    @Autowired
    EmployeeService service;

    @GetMapping()
    public @ResponseBody
    List<Employee> get(@RequestParam(value = "status", required = false) Status status)
    {
        if(status == null){
            return service.getAll();
        }
        else
            return service.getAll(status);
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

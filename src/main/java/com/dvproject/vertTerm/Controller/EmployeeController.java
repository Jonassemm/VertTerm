package com.dvproject.vertTerm.Controller;

import com.dvproject.vertTerm.Model.Employee;
import com.dvproject.vertTerm.Service.BasicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/Employees")
public class EmployeeController {
    @Autowired
    BasicService<Employee> basicService;


    @GetMapping()
    public @ResponseBody
    List<Employee> get()
    {
        return basicService.getAll();
    }

    @GetMapping("/{id}")
    public @ResponseBody
    Employee get(@PathVariable String id)
    {
        return basicService.getById(id);
    }

    @PostMapping()
    public @ResponseBody
    Employee create(@RequestBody Employee newEmployee)
    {
        return basicService.create(newEmployee);
    }

    @PutMapping("/{id}")
    public Employee UpdateEmployee(@RequestBody Employee newEmployee)
    {
        return basicService.update(newEmployee);
    }

    @DeleteMapping("/{id}")
    public boolean DeleteEmployee(@PathVariable String id)
    {
        return basicService.delete(id);
    }
}

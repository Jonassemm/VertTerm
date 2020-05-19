package com.dvproject.vertTerm.Controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dvproject.vertTerm.Model.Employee;
import com.dvproject.vertTerm.repository.EmployeeRepository;

@RestController
@RequestMapping("/Employee")
public class EmployeeController {
    @Autowired
    EmployeeRepository repo;


    @GetMapping()
    public @ResponseBody
    List<Employee> getEmployees()
    {
        return repo.findAll();
    }

    @GetMapping("/{id}")
    public @ResponseBody
    Employee getEmployee(@PathVariable String id)
    {
        Optional<Employee> employee = repo.findById(id);
        return employee.orElse(null);
    }

    @PostMapping()
    public Employee CreateEmployee(@RequestBody Employee newEmployee)
    {
        if(!repo.existsById(newEmployee.getId()))
            return repo.save(newEmployee);
        else return null;
    }

    @PutMapping("/{id}")
    public Employee UpdateEmployee(@RequestBody Employee newEmployee, @PathVariable String id)
    {
        Optional<Employee> oldEmployee = repo.findById(id);
        if(oldEmployee.isPresent() && newEmployee.getId().equals(id)){
            return repo.save(newEmployee);
        }
        else return null;
    }

    @DeleteMapping("/{id}")
    public boolean DeleteEmployee(@PathVariable String id)
    {
        repo.deleteById(id);
        return !repo.existsById(id);
    }
}

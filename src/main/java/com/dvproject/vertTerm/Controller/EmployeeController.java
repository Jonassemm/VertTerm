package com.dvproject.vertTerm.Controller;

import com.dvproject.vertTerm.Model.Employee;
import com.dvproject.vertTerm.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

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

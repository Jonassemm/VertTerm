package com.dvproject.vertTerm.Controller;

import com.dvproject.vertTerm.Model.Availability;
import com.dvproject.vertTerm.Model.Employee;
import com.dvproject.vertTerm.Model.Resource;
import com.dvproject.vertTerm.Model.Status;
import com.dvproject.vertTerm.Service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/Employees")
public class EmployeeController {
    @Autowired
    EmployeeService service;

    @GetMapping()
    public @ResponseBody
    List<Employee> get(@RequestParam(value = "status", required = false) Status status,
                       @RequestParam(value = "position", required = false) String positionId)
    {
        if (status != null) {
            return service.getAll(status);
        }
        if (positionId != null) {
            return service.getAll(positionId);
        }
        else {
            return service.getAll();
        }
    }

    @GetMapping("/{id}")
    public @ResponseBody
    Employee get(@PathVariable String id)
    {
        return service.getById(id);
    }
    
    @GetMapping("/Availability/{id}")
	public List<Availability> getResourceAvailability (@PathVariable String id) {
		return service.getAllAvailabilities(id);
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

    @GetMapping("/EmpbyPosandStatus")
	 public  @ResponseBody  List<Employee> getEmployeesByPositionIdandStatus(@RequestParam String positionId,@RequestParam Status status) {
	     return service.getEmployeesByPositionIdandStatus(positionId, status);
	}
	 
    @DeleteMapping("/{id}")
    public boolean DeleteEmployee(@PathVariable String id)
    {
   	 return service.delete(id);
    }
}

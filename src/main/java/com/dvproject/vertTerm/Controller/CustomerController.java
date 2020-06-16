package com.dvproject.vertTerm.Controller;

import com.dvproject.vertTerm.Model.Customer;
import com.dvproject.vertTerm.Model.Employee;
import com.dvproject.vertTerm.Model.Status;
import com.dvproject.vertTerm.Service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/Customers")
public class CustomerController {
    @Autowired
    CustomerService service;

    @GetMapping()
    public @ResponseBody
    List<Customer> get(@RequestParam(value = "status", required = false) Status status)
    {
        if(status == null){
            return service.getAll();
        }
        else
            return service.getAll(status);
    }

    @GetMapping("/{id}")
    public @ResponseBody
    Customer get(@PathVariable String id)
    {
        return service.getById(id);
    }

    @PostMapping()
    public @ResponseBody
    Customer create(@RequestBody Customer newCustomer)
    {
        return service.create(newCustomer);
    }

    @PutMapping("/{id}")
    public Customer UpdateCustomer(@RequestBody Customer newCustomer)
    {
        return service.update(newCustomer);
    }

    @DeleteMapping("/{id}")
    public boolean DeleteCustomer(@PathVariable String id)
    {
        Customer user = this.get(id);
        user.setSystemStatus(Status.DELETED);
        return this.UpdateCustomer(user).equals(user);
    }
}

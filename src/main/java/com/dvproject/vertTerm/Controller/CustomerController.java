package com.dvproject.vertTerm.Controller;

import com.dvproject.vertTerm.Model.Customer;
import com.dvproject.vertTerm.Service.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/Customers")
public class CustomerController {
    @Autowired
    Service<Customer> service;


    @GetMapping()
    public @ResponseBody
    List<Customer> get()
    {
        return service.getAll();
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
        return service.delete(id);
    }
}

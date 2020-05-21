package com.dvproject.vertTerm.Controller;

import com.dvproject.vertTerm.Model.Customer;
import com.dvproject.vertTerm.Service.BasicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/Customers")
public class CustomerController {
    @Autowired
    BasicService<Customer> basicService;


    @GetMapping()
    public @ResponseBody
    List<Customer> get()
    {
        return basicService.getAll();
    }

    @GetMapping("/{id}")
    public @ResponseBody
    Customer get(@PathVariable String id)
    {
        return basicService.getById(id);
    }

    @PostMapping()
    public @ResponseBody
    Customer create(@RequestBody Customer newCustomer)
    {
        return basicService.create(newCustomer);
    }

    @PutMapping("/{id}")
    public Customer UpdateCustomer(@RequestBody Customer newCustomer)
    {
        return basicService.update(newCustomer);
    }

    @DeleteMapping("/{id}")
    public boolean DeleteCustomer(@PathVariable String id)
    {
        return basicService.delete(id);
    }
}

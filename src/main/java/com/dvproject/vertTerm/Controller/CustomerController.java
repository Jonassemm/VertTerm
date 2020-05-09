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

import com.dvproject.vertTerm.Model.Customer;
import com.dvproject.vertTerm.repository.CustomerRepository;

@RestController
@RequestMapping("/api/Customer")
public class CustomerController {
    @Autowired
    CustomerRepository repo;


    @GetMapping()
    public @ResponseBody
    List<Customer> getCustomer()
    {
        return repo.findAll();
    }

    @GetMapping("/{id}")
    public @ResponseBody
    Customer getCustomer(@PathVariable String id)
    {
        Optional<Customer> customer = repo.findById(id);
        return customer.orElse(null);
    }

    @PostMapping()
    public Customer CreateCustomer(@RequestBody Customer newCustomer)
    {
        if(!repo.existsById(newCustomer.getId()))
            return repo.save(newCustomer);
        else return null;
    }

    @PutMapping("/{id}")
    public Customer UpdateCustomer(@RequestBody Customer newCustomer, @PathVariable String id)
    {
        Optional<Customer> oldCustomer = repo.findById(id);
        if(oldCustomer.isPresent() && newCustomer.getId().equals(id)){
            return repo.save(newCustomer);
        }
        else return null;
    }

    @DeleteMapping("/{id}")
    public boolean DeleteCustomer(@PathVariable String id)
    {
        repo.deleteById(id);
        return !repo.existsById(id);
    }
}

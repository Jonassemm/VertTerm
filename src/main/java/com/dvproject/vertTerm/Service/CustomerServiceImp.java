package com.dvproject.vertTerm.Service;

import com.dvproject.vertTerm.Model.Customer;
import com.dvproject.vertTerm.Model.Employee;
import com.dvproject.vertTerm.Model.OptionalAttribute;
import com.dvproject.vertTerm.Model.Status;
import com.dvproject.vertTerm.Model.User;
import com.dvproject.vertTerm.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CustomerServiceImp implements CustomerService {

    @Autowired
    CustomerRepository repo;
    
    @Autowired
    private UserService userService;

    @Override
    public List<Customer> getAll() {
        return repo.findAll();
    }

    //@PreAuthorize("hasAuthority('USERS_DATA_READ')")
    public List<Customer> getAll(Status status) {
        List<Customer> users = null;
        switch(status){
            case ACTIVE:
                users = repo.findAllActive();
                break;
            case INACTIVE:
                users = repo.findAllInactive();
                break;
            case DELETED:
                users = repo.findAllDeleted();
                break;
        }
        return (users);
    }

    @Override
    public Customer getById(String id) {
        Optional<Customer> appointment = repo.findById(id);
        return appointment.orElse(null);
    }

    @Override
    public Customer create(Customer newInstance) {
        if (newInstance.getId() == null) {
        	newInstance.setAvailabilities(null);
        	userService.testMandatoryFields(newInstance);
        	userService.encodePassword(newInstance);
            return repo.save(newInstance);
        }
        if (repo.findById(newInstance.getId()).isPresent()) {
            throw new ResourceNotFoundException("Instance with the given id (" + newInstance.getId() + ") exists on the database. Use the update method.");
        }
        return null;
    }

    @Override
    public Customer update(Customer updatedInstance) {
        if (updatedInstance.getId() != null && repo.findById(updatedInstance.getId()).isPresent()) {
        	userService.testMandatoryFields(updatedInstance);
        	userService.encodePassword(updatedInstance);
            return repo.save(updatedInstance);
        }
        return null;
    }

    @Override
    public boolean delete(String id) {
    	userService.testAppointments(id);
        repo.deleteById(id);
        return repo.existsById(id);
    }
}

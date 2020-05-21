package com.dvproject.vertTerm.Service;

import com.dvproject.vertTerm.Model.Employee;
import com.dvproject.vertTerm.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EmployeeServiceImp implements BasicService<Employee> {

    @Autowired
    EmployeeRepository repo;

    @Override
    public List<Employee> getAll() {
        return repo.findAll();
    }

    @Override
    public Employee getById(String id) {
        Optional<Employee> appointment = repo.findById(id);
        return appointment.orElse(null);
    }

    @Override
    public Employee create(Employee newInstance) {
        if (newInstance.getId() == null) {
            return repo.save(newInstance);
        }
        if (repo.findById(newInstance.getId()).isPresent()) {
            throw new ResourceNotFoundException("Instance with the given id (" + newInstance.getId() + ") exists on the database. Use the update method.");
        }
        return null;
    }

    @Override
    public Employee update(Employee updatedInstance) {
        if (updatedInstance.getId() != null && repo.findById(updatedInstance.getId()).isPresent()) {
            return repo.save(updatedInstance);
        }
        return null;
    }

    @Override
    public boolean delete(String id) {
        repo.deleteById(id);
        return repo.existsById(id);
    }
}

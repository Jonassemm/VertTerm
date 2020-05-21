package com.dvproject.vertTerm.Service;

import com.dvproject.vertTerm.Model.Appointment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;

import java.util.List;
import java.util.Optional;

public class AppointmentServiceImpl implements Service<Appointment> {

    @Autowired
    MongoRepository<Appointment, String> repo;

    @Override
    public List<Appointment> getAll() {
        return repo.findAll();
    }

    @Override
    public Appointment getById(String id) {
        Optional<Appointment> appointment = repo.findById(id);
        return appointment.orElse(null);
    }

    @Override
    public Appointment create(Appointment newInstance) {
        if (newInstance.getId() == null) {
            return repo.save(newInstance);
        }
        if (repo.findById(newInstance.getId()).isPresent()) {
            throw new ResourceNotFoundException("Instance with the given id (" + newInstance.getId() + ") exists on the database. Use the update method.");
        }
        return null;
    }

    @Override
    public Appointment update(Appointment updatedInstance) {
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

package com.dvproject.vertTerm.Service;

import com.dvproject.vertTerm.Model.*;
import com.dvproject.vertTerm.repository.AppointmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class AppointmentServiceImpl implements BasicService<Appointment> {

    @Autowired
    AppointmentRepository repo;

    @Override
    public List<Appointment> getAll() {
        return repo.findAll();
    }

    public List<Appointment> getAll(Bookable bookable){
        List<Appointment> result = new ArrayList<>();
        for(Appointment appointment : this.getAll()){
            if(appointment.getBookedCustomer().getId().equals(bookable.getId())){
                result.add(appointment);
            }
            for(Employee employee : appointment.getBookedEmployees()){
                if(employee.getId().equals(bookable.getId())){
                    result.add(appointment);
                }
            }
            for(Resource resource : appointment.getBookedResources()){
                if(resource.getId().equals(bookable.getId())){
                    result.add(appointment);
                }
            }
        }
        return result;
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
    
    public List<Appointment> getAppointmentsOfBookedEmployeeInTimeinterval(String employeeid, Date starttime, Date endtime, AppointmentStatus status){
    	return repo.findAppointmentsByBookedEmployeeInTimeinterval(employeeid, starttime, endtime, status);
    }
    
    public List<Appointment> getAppointmentsOfBookedResourceInTimeinterval(String resourceid, Date starttime, Date endtime, AppointmentStatus status){
    	return repo.findAppointmentsByBookedResourceInTimeinterval(resourceid, starttime, endtime, status);
    }
    
    public List<Appointment> getAppointmentsOfBookedCustomerInTimeinterval(String userid, Date starttime, Date endtime, AppointmentStatus status){
    	return repo.findAppointmentsByBookedCustomerInTimeinterval(userid, starttime, endtime, status);
    }
    
    public List<Appointment> getAppointmentsWithUseridAndTimeInterval(String userid, Date starttime, Date endtime){
    	return repo.findAppointmentsByBookedUserAndTimeinterval(userid, starttime, endtime);
    }
    
    public List<Appointment> getAppointmentsInTimeIntervalWithStatus(Date starttime, Date endtime, AppointmentStatus status){
    	return repo.findAppointmentsByTimeintervalAndStatus(starttime, endtime, status);
    }
    
    public List<Appointment> getAppointmentsInTimeInterval(Date starttime, Date endtime){
    	return repo.findAppointmentsByTimeinterval(starttime, endtime);
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

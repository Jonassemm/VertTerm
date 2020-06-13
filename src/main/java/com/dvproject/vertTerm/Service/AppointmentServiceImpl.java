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
public class AppointmentServiceImpl implements AppointmentService {

    @Autowired
    AppointmentRepository repo;

    @Override
    public List<Appointment> getAll() {
        return repo.findAll();
    }

    @Override
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
    
    @Override
    public List<Appointment> getAppointmentsByUserid(String id) {
    	return repo.findByBookedCustomerId(id);
    }
    
    @Override
    public List<Appointment> getAppointmentsByUserid(String id, AppointmentStatus appointmentStatus){
    	return repo.findByBookedCustomerIdAndStatus(id, appointmentStatus);
    }
    
    @Override
    public List<Appointment> getAppointmentsOfBookedEmployeeInTimeinterval(String employeeid, Date starttime, Date endtime, AppointmentStatus status){
    	return repo.findAppointmentsByBookedEmployeeInTimeinterval(employeeid, starttime, endtime, status);
    }
    
    @Override
    public List<Appointment> getAppointmentsOfBookedResourceInTimeinterval(String resourceid, Date starttime, Date endtime, AppointmentStatus status){
    	return repo.findAppointmentsByBookedResourceInTimeinterval(resourceid, starttime, endtime, status);
    }
    
    @Override
    public List<Appointment> getAppointmentsOfBookedCustomerInTimeinterval(String userid, Date starttime, Date endtime, AppointmentStatus status){
    	return repo.findAppointmentsByBookedCustomerInTimeinterval(userid, starttime, endtime, status);
    }
    
    @Override
    public List<Appointment> getAppointmentsOfBookedProcedureInTimeinterval(String procedureid, Date starttime, Date endtime, AppointmentStatus status){
    	return repo.findAppointmentsByBookedProceudreInTimeinterval(procedureid, starttime, endtime, status);
    }
    
    @Override
    public List<Appointment> getAppointmentsWithUseridAndTimeInterval(String userid, Date starttime, Date endtime){
    	return repo.findAppointmentsByBookedUserAndTimeinterval(userid, starttime, endtime);
    }
    
    @Override
    public List<Appointment> getAppointmentsInTimeIntervalWithStatus(Date starttime, Date endtime, AppointmentStatus status){
    	return repo.findAppointmentsByTimeintervalAndStatus(starttime, endtime, status);
    }
    
    @Override
    public List<Appointment> getAppointmentsInTimeInterval(Date starttime, Date endtime){
    	return repo.findAppointmentsByTimeinterval(starttime, endtime);
    }
    
    @Override
	public List<Appointment> getAppointments(Available available, Date endOfSeries) {
    	return available.getAppointmentsAfterDate(this, endOfSeries);
	}
    
    @Override
	public List<Appointment> getAppointmentsOfEmployee(String employeeid, Date startdate) {
    	return repo.findByBookedEmployeeId(employeeid, startdate);
	}
    
    @Override
	public List<Appointment> getAppointmentsOfProcedure(String procedureid, Date startdate) {
    	return repo.findByBookedProcedureId(procedureid, startdate);
    }
    
    @Override
	public List<Appointment> getAppointmentsOfResource(String resourceid, Date startdate) {
    	return repo.findByBookedResourceId(resourceid, startdate);
	}
    
	@Override
	public List<Appointment> getAppointmentsByWarning(Warning warning) {
		return repo.findByWarnings(warning);
	}
	
	public List<Appointment> getAppointmentsByWarnings(List<Warning> warnings){
		return repo.findByWarningsIn(warnings);
	}

	@Override
	public List<Appointment> getAppointmentsByWarningAndId(String userid, Warning warning) {
		return repo.findByBookedCustomerIdAndWarnings(userid, warning);
	}

	@Override
	public List<Appointment> getAppointmentsByWarningsAndId(String userid, List<Warning> warnings) {
		return repo.findByBookedCustomerIdAndWarningsIn(userid, warnings);
	}

    @Override
    public Appointment update(Appointment updatedInstance) {
        if (updatedInstance.getId() != null && repo.findById(updatedInstance.getId()).isPresent()) {
            return repo.save(updatedInstance);
        }
        return null;
    }
    
    @Override
    public boolean setCustomerIsWaiting(String id, boolean customerIsWaiting) {
    	Appointment appointment = this.getById(id);
    	
    	if(appointment.getActualStarttime() == null && appointment.getActualEndtime() == null && 
    			appointment.getStatus() == AppointmentStatus.PLANNED) {
    		throw new IllegalArgumentException("Customer of this appointment can not be set");
    	}
    	
    	if (!StatusService.isUpdateable(appointment.getBookedCustomer().getSystemStatus())) {
    		throw new IllegalArgumentException("Customer can not be updated");
    	}
    	
    	appointment.setCustomerIsWaiting(customerIsWaiting);
    	
    	return repo.save(appointment).isCustomerIsWaiting() == customerIsWaiting;
    }

    @Override
    public boolean delete(String id) {
        repo.deleteById(id);
        return repo.existsById(id);
    }
}

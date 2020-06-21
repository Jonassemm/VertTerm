package com.dvproject.vertTerm.Service;

import com.dvproject.vertTerm.Model.*;
import com.dvproject.vertTerm.repository.AppointmentRepository;

import sun.security.krb5.internal.APOptions;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class AppointmentServiceImpl implements AppointmentService {

    @Autowired
    AppointmentRepository repo;
    @Autowired
    ResourceService ResSer;
    @Autowired
    EmployeeService EmpSer;
    @Autowired
    ProcedureService ProcedureSer;
    @Autowired
    RestrictionService RestrictionSer;
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
    public List<Appointment> getAppointmentsByEmployeeid(String id) {
    	return repo.findByBookedEmployeesId(id);
    }
    
    @Override
	public List<Appointment> getAppointmentsOfEmployee(String employeeid, Date startdate) {
    	return repo.findByBookedEmployeesIdAndPlannedStarttimeAfter(employeeid, startdate);
	}
    
    @Override
	public List<Appointment> getAppointmentsOfProcedure(String procedureid, Date startdate) {
    	return repo.findByBookedProcedureIdAndPlannedStarttimeAfter(procedureid, startdate);
    }
    
    @Override
	public List<Appointment> getAppointmentsOfResource(String resourceid, Date startdate) {
    	return repo.findByBookedResourcesIdAndPlannedStarttimeAfter(resourceid, startdate);
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
    
      public Res_Emp getAvailableResourcesAndEmployees(Appointmentgroup group) { 	
    	Res_Emp list= new Res_Emp();
    	List<Employee> Employees=new ArrayList<>();
    	List<Resource> Resources=new ArrayList<>();
        // get all appointments from Appointmentgroup
    	List<Appointment> appointments=group.getAppointments();
    	//for each appointment	
    	//1-get Booked Procedure
		//2-get Procedure from DB
		//3-get Needed ResourceTypes 
    	//4-get Needed Employee Positions
    	for (Appointment appointment : appointments) {
    		Procedure procedureOfAppointment = appointment.getBookedProcedure();   		
    		Procedure procedure = ProcedureSer.getById(procedureOfAppointment.getId());
    		List<ResourceType> ResourceTypes=procedure.getNeededResourceTypes();
    		List<Position> Positions=procedure.getNeededEmployeePositions();	
    		//for each ResourceType
    		//get all resources from this Type and for each one check if:
    		//1- Resource Available to the appointment 
    		//and 2- Check Restrictions of Resource and Procedure
    		for (ResourceType rt : ResourceTypes) {
	    		for (Resource resource : ResSer.getAll(rt))   		
	    			{ 
	    			if (ResSer.isResourceAvailableBetween(resource.getId(), appointment.getPlannedStarttime(), appointment.getPlannedEndtime())
	    				 && RestrictionSer.testRestrictions(resource.getRestrictions(), procedure.getRestrictions()) )
	    				Resources.add(resource);
	    			}
	    	}
    		//for each position
    		//get all Employees who has this Position and for each one check if:
    		//1- Employee Available to the appointment 
    		//and 2- Check Restrictions of Employee and Procedure   
	    	for (Position pos : Positions) {
	    		for (Employee employee : EmpSer.getAll(pos.getId()))
		    		{
	    			if (EmpSer.isEmployeeAvailableBetween(employee.getId(), appointment.getPlannedStarttime(), appointment.getPlannedEndtime())
	    				 && RestrictionSer.testRestrictions(employee.getRestrictions(), procedure.getRestrictions()) )
							Employees.add(employee);
	    		    }
			}
    	}

    	list.setResources(Resources);
    	list.setEmployees(Employees);
    	return list;
	}
}

package com.dvproject.vertTerm.Service;

import com.dvproject.vertTerm.Model.*;
import com.dvproject.vertTerm.exception.AppointmentTimeException;
import com.dvproject.vertTerm.repository.AppointmentRepository;

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
    private AppointmentgroupService appointmentgroupService;

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
    	
    	if(appointment.getActualStarttime() != null && appointment.getActualEndtime() != null && 
    			appointment.getStatus() != AppointmentStatus.PLANNED) {
    		throw new IllegalArgumentException("Customer of this appointment can not be set");
    	}
    	
    	if (!StatusService.isUpdateable(appointment.getBookedCustomer().getSystemStatus())) {
    		throw new IllegalArgumentException("Customer can not be updated");
    	}
    	
    	appointment.setCustomerIsWaiting(customerIsWaiting);
    	appointment = repo.save(appointment);
    	
    	appointmentgroupService.setPullableAppointment(appointment);
    	
    	return appointment.isCustomerIsWaiting() == customerIsWaiting;
    }

    @Override
    public boolean delete(String id) {
    	Appointment appointment = getById(id);
    	
    	appointment.setStatus(AppointmentStatus.DELETED);
    	
    	repo.save(appointment);
    	
    	return getById(id).getStatus() == AppointmentStatus.DELETED;
    }
    
      public Appointmentgroup getAvailableResourcesAndEmployees(Appointmentgroup group) { 	   
    	List<Employee> Employees=new ArrayList<>();
    	List<Resource> Resources=new ArrayList<>();
        //get all appointments from Appointmentgroup
    	List<Appointment> appointments=group.getAppointments();
    	//Test 
    	//Procedure Relations
    	//each Appointment 
    	group.testProcedureRelations();
    	List <TimeInterval> timelist = new ArrayList<> ();
        group.getAppointments().forEach(app -> new NormalBookingTester().testAppointmentTimes(timelist));
    	//for each appointment	
    	//1-get Booked Procedure
		//2-get Needed ResourceTypes 
    	//3-get Needed Employee Positions
    	for (Appointment appointment : appointments) {
    		Procedure procedureOfAppointment = appointment.getBookedProcedure();   		
    		List<ResourceType> ResourceTypes=procedureOfAppointment.getNeededResourceTypes();
    		List<Position> Positions=procedureOfAppointment.getNeededEmployeePositions();	
    		//for each ResourceType
    		//get all resources from this Type and for each one check if:
    		//Resource has any other appointment/s in the Timeinterval 
    		//if no appointment/s could be found then add the resource to the "Resouces" List
    		//throw an Exception if no resource available
    		for (ResourceType rt : ResourceTypes) {
    			    boolean Resourcefound=false;
		    		for (Resource resource : ResSer.getAll(rt))   
		    	  	{ 
	    			 List<Appointment> ResApps=this.getAppointmentsOfBookedResourceInTimeinterval(resource.getId(),
	    	          		  appointment.getPlannedStarttime(), appointment.getActualEndtime(),AppointmentStatus.PLANNED);    		
	    			 if (ResApps.size() == 0 )
	    			  {
	    				 Resources.add(resource);
	    				 Resourcefound=true;
	    			     break;
	    			  }
	    			}
		    		if(!(Resourcefound))
	    				throw new AppointmentTimeException("No resource from type" + rt.getName()+" is available",appointment);
	    	}
    		//for each position
    		//get all Employees who has this Position and for each one check if:
    		//Employee has any other appointment/s in the Timeinterval 
    		//if no appointment/s could be found then add the employee to the "Employees" List
    		//throw an Exception if no employee available
	    	for (Position pos : Positions) {
	    	    boolean Employeefound=false;
	    		for (Employee employee : EmpSer.getAll(pos.getId()))
		    		{
	    			 List<Appointment> EmpApps=this.getAppointmentsOfBookedEmployeeInTimeinterval(employee.getId(),
	    	          		  appointment.getPlannedStarttime(), appointment.getActualEndtime(),AppointmentStatus.PLANNED) ;
	    			 if (EmpApps.size() == 0 )
	    			 {  
	    				 Employees.add(employee); 
	    				 Employeefound=true;
	    			 	  break;
	    			 	}
	    			}
		    		 if(!(Employeefound))
	    				throw new AppointmentTimeException("no employee from position" + pos.getName()+" is available",appointment);
	    		    }
	    	//set Resources and Employees to the appointment
	    	appointment.setBookedResources(Resources);
	    	appointment.setBookedEmployees(Employees);
	    	//throw exceptions if not all needed employees/resources could be found
	     	if(Positions.size() != appointment.getBookedEmployees().size())
	    		throw new AppointmentTimeException("In the time interval not all needed employees for appointment could be found" ,appointment);
	     	if(Resources.size() != appointment.getBookedResources().size())
	    		throw new AppointmentTimeException("In the time interval not all needed resources for appointment could be found",appointment);		     
    	}

    	return group;
	}
}

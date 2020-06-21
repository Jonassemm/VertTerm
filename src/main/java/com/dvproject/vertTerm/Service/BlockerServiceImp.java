package com.dvproject.vertTerm.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dvproject.vertTerm.Model.Appointment;
import com.dvproject.vertTerm.Model.Availability;
import com.dvproject.vertTerm.Model.Blocker;
import com.dvproject.vertTerm.Model.Employee;
import com.dvproject.vertTerm.Model.Resource;
import com.dvproject.vertTerm.Model.Warning;
import com.dvproject.vertTerm.repository.AppointmentRepository;
import com.dvproject.vertTerm.repository.BlockerRepository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;


@Service
@Transactional
public class BlockerServiceImp implements BlockerService {

   @Autowired
   private BlockerRepository blockerRepo;
   @Autowired
   private AppointmentRepository AppoRepo;

   @Autowired
   private AppointmentService AppoService;
   @Autowired
	private AvailabilityServiceImpl availabilityService;

   
   @Autowired
   private EmployeeService EmpService;
   @Autowired
   private ResourceService ResService;

   public Blocker create(Blocker blocker) {
	  Blocker b= blockerRepo.save(blocker);
      SetWarningFlag(blocker.getId());
      return b;
   }
   
   public List<Appointment> SetWarningFlag(String id) {
	   List<Appointment> appos=new ArrayList<>();
	   Optional <Blocker> BlockerDb = this.blockerRepo.findById(id);
       if (BlockerDb.isPresent()) {
    	   Blocker blocker=  BlockerDb.get();
    	
    	   //get all employees from DB
    	   List<Employee> emps=EmpService.getAll();
    	   //get all Resources from DB
    	   List<Resource> ress=ResService.getAll();
    	   for(Employee emp : emps )
    	   {   
    		   //get Employee's Availability list
    		   List<Availability> EmpAvasList=emp.getAvailabilities(); 
    		   //get all appointments from Blocker and for each one 
    		   //get an appointment by ID from DB
    		   //check if the employee available to the appointment and if not then
    		   //add EMPLOYEE_WARNING to the appointment / save it / add an Appointment to "appos" List
               for(Appointment app :  blocker.getAppointments() )
     	       {    
          		  Appointment appDB=  this.AppoService.getById(app.getId());
          		  if(!(availabilityService.isAvailable(EmpAvasList,appDB.getPlannedStarttime(), appDB.getActualEndtime()) ) )
   		   	   	  {  
               		   appDB.addWarning(Warning.EMPLOYEE_WARNING);
             		   AppoRepo.save(appDB);
                       appos.add(app);
                   }
          		  else
          			 appos.add(app);
                }
		   }
    	   for(Resource res : ress )
    	   {	
    		   //get Resource's Availability list
    		   List<Availability> ResAvasList=res.getAvailabilities();
    		   //get all appointments from Blocker and for each one 
    		   //get an appointment by ID from DB
    		   //check if the Resource available to the appointment and if not then
    		   //add RESOURCE_WARNING to the appointment
    		     for(Appointment app : blocker.getAppointments() )
    		     {
    		    	  Appointment appDB=  this.AppoService.getById(app.getId());
              		  if(!(availabilityService.isAvailable(ResAvasList,appDB.getPlannedStarttime(), appDB.getActualEndtime()) ) )
       		   	   	  {  
                   		   appDB.addWarning(Warning.RESOURCE_WARNING);
                 		   AppoRepo.save(appDB);
                           appos.add(app);
	                   }
              		  else  
              			  appos.add(app);
    		     }
    	   }
    	
    	//save changed Appointments to the blocker
    	blocker.setAppointments(appos);
       }
   
       else {
    		throw new ResourceNotFoundException("Blocker with the given id :" +id + " not found");
       }
       
      return appos;
}


   public Blocker update(Blocker Blocker) {
	   if (blockerRepo.findById(Blocker.getId()).isPresent()) {
			return blockerRepo.save(Blocker);
		}
	   else {
	  	throw new ResourceNotFoundException("Blocker with the given id :" +Blocker.getId() + "not found");  
	   } 	
   }
   
  
   public List <Blocker> getAll() {
       return this.blockerRepo.findAll();
       
   }
   
   public Blocker getById(String id) {
	   Optional <Blocker> BlockerDb = this.blockerRepo.findById(id);
       if (BlockerDb.isPresent()) {
           return BlockerDb.get();
       } else {
    		throw new ResourceNotFoundException("Blocker with the given id :" +id + " not found");
       }
   }
   
	public List<Blocker> getBlockers(String[] ids) {
	   	List<Blocker> Blockers = new ArrayList<> ();

	   	for (String id : ids) {
	   		Blockers.add(this.getById(id));
	   	}

	   	return Blockers;
	}

   public boolean delete(String id) {
     Optional <Blocker> BlockerDb = this.blockerRepo.findById(id);
       if (BlockerDb.isPresent()) {
           this.blockerRepo.delete(BlockerDb.get());
       }      
   	    else {
    	     throw new ResourceNotFoundException("Blocker with the given id : " + id  + " not found ");
   		  
       }
       return blockerRepo.existsById(id);
    }

	
	
}





package com.dvproject.vertTerm.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dvproject.vertTerm.Model.Appointment;
import com.dvproject.vertTerm.Model.AppointmentStatus;
import com.dvproject.vertTerm.Model.Availability;
import com.dvproject.vertTerm.Model.Blocker;
import com.dvproject.vertTerm.Model.Employee;
import com.dvproject.vertTerm.Model.Resource;
import com.dvproject.vertTerm.Model.Warning;
import com.dvproject.vertTerm.exception.AppointmentTimeException;
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
   

   public Blocker create(Blocker blocker) {
	      if(this.blockerRepo.findByname(blocker.getName()) == null) {
	          blockerRepo.save(blocker);
	          blocker.setName(capitalize(blocker.getName()));  	    	 
	    	  SetWarningFlag(blocker.getId());
	          return blocker;
	          }
	        else {
	  	    	throw new ResourceNotFoundException("Blocker with the given id :" +blocker.getId() + "already exsist");  
	  	    } 

   }
   
   public void SetWarningFlag(String id) {
	  // get Blocker by ID from DB
	   Optional <Blocker> BlockerDb = this.blockerRepo.findById(id);
	   //if Blocker exists
       if (BlockerDb.isPresent()) {
    	   Blocker blocker=  BlockerDb.get();
    	   		// for each employee in Blocker's BookedEmployees List
    	   		// get all appointments from the employee in the time interval of the blocker appointment
    	   		// and for each appointment set "AppointmentWarning" warning flag
    		   for(Employee emp : blocker.getBookedEmployees() )
    		   {   
          		  List<Appointment> EmpApps=this.AppoService.getAppointmentsOfBookedEmployeeInTimeinterval(emp.getId(),
          		  blocker.getPlannedStarttime(), blocker.getActualEndtime(),AppointmentStatus.PLANNED) ;
          		 if(EmpApps.size() > 0)
          		 {  
          			 for(Appointment app : EmpApps ){  
	          			   Appointment appDB=  this.AppoService.getById(blocker.getId());
	               		   appDB.addWarning(Warning.APPOINTMENT_WARNING);
	             		   AppoRepo.save(appDB);
	                   	}
	    		   }
	       		  else 
	       		  {
	       			throw new ResourceNotFoundException("No appointments from employee: "+emp.getFirstName() +" "+ emp.getLastName() +" in the time interval of the blocker appointment could be found");  
	       		  }
    		   }
    	     // for each resource in Blocker's BookedResources List
   	   		// get all appointments from the resource in the time interval of the blocker appointment
   	   		//and for each appointment set "AppointmentWarning" warning flag   		    
    	   for(Resource res : blocker.getBookedResources() )
    	   {	
          		  List<Appointment> ResApps=this.AppoService.getAppointmentsOfBookedResourceInTimeinterval(res.getId(),
          		  blocker.getPlannedStarttime(), blocker.getActualEndtime(),AppointmentStatus.PLANNED) ;
          		  if(ResApps.size() > 0)
          		  {
          			  for(Appointment app : ResApps ){  
          			   Appointment appDB=  this.AppoService.getById(blocker.getId());
               		   appDB.addWarning(Warning.APPOINTMENT_WARNING);
             		   AppoRepo.save(appDB);
          			  }
          		  }
          		  else 
          		  {
          			throw new ResourceNotFoundException("No appointments from resource :"+ res.getName()+" in the time interval of the blocker appointment could be found");  
          		  }
    	   }
       }
       else {
    		throw new ResourceNotFoundException("Blocker with the given id :" +id + " not found");
       }
       
}


   public Blocker update(Blocker blocker) {
	   if (blockerRepo.findById(blocker.getId()).isPresent()) {
		   blocker.setName(capitalize(blocker.getName()));  
			return blockerRepo.save(blocker);
		}
	   else {
	  	throw new ResourceNotFoundException("Blocker with the given id :" +blocker.getId() + "not found");  
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

   public static String capitalize(String str)
   {
       if(str == null) return str;
       return  str.substring(0, 1).toUpperCase()+str.substring(1).toLowerCase();
       
   }
	
}





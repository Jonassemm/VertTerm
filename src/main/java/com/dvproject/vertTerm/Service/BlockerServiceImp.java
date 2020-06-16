package com.dvproject.vertTerm.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dvproject.vertTerm.Model.Appointment;
import com.dvproject.vertTerm.Model.Blocker;
import com.dvproject.vertTerm.Model.Warning;
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
   private AppointmentService AppoService;
   // zeitraum , list resourcen and mitarbeitern
   // 
   public Blocker create(Blocker blocker) {
	  Blocker b= blockerRepo.save(blocker);
//      SetWarningFlag(blocker.getId());
      return b;
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

	
	public void SetWarningFlag(String id) {
		//List<Appointment> appos=new ArrayList<>();
		   Optional <Blocker> BlockerDb = this.blockerRepo.findById(id);
	       if (BlockerDb.isPresent()) {
	    	   Blocker b=  BlockerDb.get();
	    	   Date BlockerStartDate= b.getStartDate();
	    	   Date BlockerEndDate= b.getEndDate();
	    	   List<Appointment> AllAppos =AppoService.getAll();
	    	   for(Appointment app : AllAppos )
	    	   {
	    		   if((app.getActualStarttime() == BlockerStartDate) && (app.getActualEndtime() == BlockerEndDate))
	    		   { 
	    			   app.addWarning(Warning.APPOINTMENT_TIME_WARNING);
	    			 //  appos.add(app);
	    		   }
	    	   }
	       }
	       else {
	    		throw new ResourceNotFoundException("Blocker with the given id :" +id + " not found");
	       }
	      // return appos;
	}




}





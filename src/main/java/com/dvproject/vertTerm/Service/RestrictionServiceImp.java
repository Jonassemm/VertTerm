package com.dvproject.vertTerm.Service;

import com.dvproject.vertTerm.Model.Restriction;
import com.dvproject.vertTerm.Model.User;
import com.dvproject.vertTerm.Model.OptionalAttribute;
import com.dvproject.vertTerm.Model.Procedure;
import com.dvproject.vertTerm.Model.Resource;
import com.dvproject.vertTerm.repository.ProcedureRepository;
import com.dvproject.vertTerm.repository.RessourceRepository;
import com.dvproject.vertTerm.repository.RestrictionRepository;
import com.dvproject.vertTerm.repository.UserRepository;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.fasterxml.jackson.core.sym.Name;

import net.springboot.javaguides.exception.ResourceExsistException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class RestrictionServiceImp implements RestrictionService {

   @Autowired
   private RestrictionRepository RestrictionRepo;
   @Autowired
   private UserRepository UserRepo;
   @Autowired
   private RessourceRepository ResRepo;
   @Autowired
   private ProcedureRepository ProRepo;

   //@PreAuthorize("hasAuthority('RestrictionS_WRITE')") 
   public Restriction create(Restriction Restriction) {
      if(this.RestrictionRepo.findByName(Restriction.getName()) == null)
	   return RestrictionRepo.save(Restriction);
      else {
	    	throw new ResourceNotFoundException("Restriction with the given id :" +Restriction.getId() + "already exsist");  
	    } 
     // return null;
   }

 
   //@PreAuthorize("hasAuthority('Restriction_RIGHTS_WRITE','RestrictionS_WRITE')")  
   public Restriction update(Restriction Restriction) {
	   if (RestrictionRepo.findById(Restriction.getId()).isPresent()) {
			return RestrictionRepo.save(Restriction);
		}
	   else {
	  	throw new ResourceNotFoundException("Restriction with the given id :" +Restriction.getId() + "not found");  
	   } 	
   }
   
  
   //@PreAuthorize("hasAuthority('RestrictionS_READ')") 
   public List <Restriction> getAll() {
       return this.RestrictionRepo.findAll();
   }
   
 
   //@PreAuthorize("hasAuthority('RestrictionS_READ')") 
   public Restriction getById(String id) {
	   Optional <Restriction> RestrictionDb = this.RestrictionRepo.findById(id);
       if (RestrictionDb.isPresent()) {
           return RestrictionDb.get();
       } else {
    		throw new ResourceNotFoundException("Restriction with the given id :" +id + " not found");
       }
   }
   
	// @PreAuthorize("hasAuthority('USER_Restriction_READ')") 
	public List<Restriction> getRestrictions(String[] ids) {
	   	List<Restriction> Restrictions = new ArrayList<> ();

	   	for (String id : ids) {
	   		Restrictions.add(this.getById(id));
	   	}

	   	return Restrictions;
	}

   public boolean delete(String id) {
     Optional <Restriction> RestrictionDb = this.RestrictionRepo.findById(id);
       if (RestrictionDb.isPresent()) {
           this.RestrictionRepo.delete(RestrictionDb.get());
       }      
   	    else {
    	     throw new ResourceNotFoundException("Restriction with the given id : " + id  + " not found ");
   		  
       }
       return RestrictionRepo.existsById(id);
    }
   
   	public boolean testRestrictions(String[] L1,String[] L2) {
   		     boolean Result=true;
		     for (String elmL1 : L1 )
			  {   
				 	 for (String elmL2 : L2 )
					  {    	
					     if (!(elmL1.equals(elmL2)) )
					    	 Result &= true;					     
					     else
					    	 Result &= false;
			 		     }
		      }
   		return Result;
     }



	
}


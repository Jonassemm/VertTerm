package com.dvproject.vertTerm.Service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

//import com.dvproject.vertTerm.Model.Right;
import com.dvproject.vertTerm.Model.Role;
//import com.dvproject.vertTerm.repository.RightRepository;
import com.dvproject.vertTerm.repository.RoleRepository;


@Service
@Transactional
public class RoleServiceImp implements RoleService {

   @Autowired
   private RoleRepository RoleRepo;
    
   @Override
   public Role createRole(Role role) {
      if(this.RoleRepo.findByName(role.getName()) == null)
	   return RoleRepo.save(role);
      else 
       return null;
   }

   @Override
   public Role updateRole(Role role) {
	   Optional <Role> RoleDb = this.RoleRepo.findById(role.getId());

	    if (RoleDb.isPresent()) {
	        Role RoleUpdate = RoleDb.get();
	        RoleUpdate.setId(role.getId());
	        RoleUpdate.setName(role.getName());
	        RoleUpdate.setRights(role.getRights());
	        RoleRepo.save(RoleUpdate);
	        return RoleUpdate;
	    } 
	    else {
	        throw new ResourceNotFoundException("Record not found with id : " + role.getId());
	    } 
   }
     
   @Override
   public List <Role> getAllRoles() {
       return this.RoleRepo.findAll();
   }
   

   @Override
   public Role getRoleById(String id) {
	   Optional <Role> RoleDb = this.RoleRepo.findById(id);
       if (RoleDb.isPresent()) {
           return RoleDb.get();
       } else {
           throw new ResourceNotFoundException("Record not found with id : " + id);
       }
   }
   

   @Override
   public void deleteRoleById(String id) {
       Optional <Role> RoleDb = this.RoleRepo.findById(id);

       if (RoleDb.isPresent()) {
           this.RoleRepo.delete(RoleDb.get());
       } else {
           throw new ResourceNotFoundException("Record not found with id : " + id);
       }

   }

}





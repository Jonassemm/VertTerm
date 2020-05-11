package com.dvproject.vertTerm.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dvproject.vertTerm.Service.RightService;
import com.dvproject.vertTerm.Model.Right;
import com.dvproject.vertTerm.Model.Role;
import com.dvproject.vertTerm.Model.User;
import com.dvproject.vertTerm.repository.RightRepository;
import com.dvproject.vertTerm.repository.RoleRepository;
import com.dvproject.vertTerm.repository.UserRepository;


@Service
public class RightServiceImp implements RightService {

   @Autowired
   private RightRepository RightRepo;
   @Autowired
   private UserRepository userRepo;
   @Autowired
   private RoleRepository roleRepo;
   
    
   //@PreAuthorize("hasAuthority('RIGHTS_READ')")
   public List <Right> getAllRights() {
       return this.RightRepo.findAll();
   }
   
    
   //@PreAuthorize("hasAuthority('RIGHTS_READ')") 
   public Right getRightById(String id) {
	   Optional <Right> RightDb = this.RightRepo.findById(id);
       if (RightDb.isPresent()) {
           return RightDb.get();
       } else {
           throw new ResourceNotFoundException("Record not found with id : " + id);
       }
   }
  
   //   @PreAuthorize("hasAuthority('USER_ROLES_READ','USER_RIGHTS_READ')")
   public List<User> getListUserswithRight(String id) {
			   List<User> userslist = new ArrayList<User>();
			   List<User> Allusers = userRepo.findAll();
			   for (User user : Allusers) {
				    for (Role role : user.getRoles()) {
				    	 for (Right right : role.getRights()) 
				    		if (right.getId().equals(id)) 
				    		     if (!userslist.contains(user)) 
				    		    	 userslist.add(user);					          
				    }
			   }
    return userslist;
    }


   //   @PreAuthorize("hasAuthority('ROLES_READ','ROLES_RIGHTS_READ')")
   public List<Role> getListRoleswithRight(String id) {
		   List<Role> listrole = new ArrayList<Role>();
		   List<Role> AllRoles = roleRepo.findAll();
		   
			    for (Role role : AllRoles) {
			    	 for (Right right : role.getRights()) 
			    		if (right.getId().equals(id)) 
			    		     if (!listrole.contains(role)) 
			    		    	 listrole.add(role);					          
			    }
		   
			return listrole;
	}
}




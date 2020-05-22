package com.dvproject.vertTerm.Service;

import com.dvproject.vertTerm.Model.Right;
import com.dvproject.vertTerm.Model.Role;
import com.dvproject.vertTerm.Model.User;
import com.dvproject.vertTerm.repository.RoleRepository;
import com.dvproject.vertTerm.repository.UserRepository;
import net.springboot.javaguides.exception.ResourceExsistException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import com.dvproject.vertTerm.Model.ResourceType;
import com.dvproject.vertTerm.Model.Right;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

//import com.dvproject.vertTerm.Model.Right;
//import com.dvproject.vertTerm.repository.RightRepository;


@Service
@Transactional
public class RoleServiceImp implements RoleService {

   @Autowired
   private RoleRepository RoleRepo;
   @Autowired
   private UserRepository userRepo;

   @Autowired
   private UserService userService;



   //@PreAuthorize("hasAuthority('ROLES_WRITE')") 
   public Role create(Role role) {
      if(this.RoleRepo.findByName(role.getName()) == null)
	   return RoleRepo.save(role);
      else 
    	throw new ResourceExsistException("Record already exists" );
     // return null;
   }

 
   //@PreAuthorize("hasAuthority('ROLE_RIGHTS_WRITE','ROLES_WRITE')")  
   public Role update(Role role) {
	   Optional <Role> RoleDb = this.RoleRepo.findById(role.getId());
	    if (RoleDb.isPresent()) {
	        Role RoleUpdate = RoleDb.get();
	        RoleUpdate.setId(role.getId());
	        RoleUpdate.setName(role.getName());
	        RoleUpdate.setDescription(role.getDescription());
	        RoleUpdate.setRights(role.getRights());
	        RoleRepo.save(RoleUpdate);
	        return RoleUpdate;
	    } 
	    else {
	        throw new ResourceNotFoundException("Record not found with id : " + role.getId());
	    } 
   }
   
  
   //@PreAuthorize("hasAuthority('ROLES_READ')") 
   public List <Role> getAll() {
       return this.RoleRepo.findAll();
   }
   
 
   //@PreAuthorize("hasAuthority('ROLES_READ')") 
   public Role getById(String id) {
	   Optional <Role> RoleDb = this.RoleRepo.findById(id);
       if (RoleDb.isPresent()) {
           return RoleDb.get();
       } else {
    		throw new ResourceNotFoundException("Resource with the given id :" +id + " already exists");
       }
   }
   
	// @PreAuthorize("hasAuthority('USER_ROLE_READ')") 
	public List<Role> getRoles(String[] ids) {
	   	List<Role> Roles = new ArrayList<Role> ();

	   	for (String id : ids) {
	   		Roles.add(this.getRoleById(id));
	   	}

	   	return Roles;
	}

	   //@PreAuthorize("hasAuthority('ROLES_WRITE')") 
	   public Role createRole(Role role) {
	      if(this.RoleRepo.findByName(role.getName()) == null)
		   return RoleRepo.save(role);
	       if (RoleRepo.findById(role.getId()).isPresent()) {
		   		throw new ResourceNotFoundException("Role with the given id :" + role.getId() + " already exists");
		   	    
		   	 }
	       return null;
		    
	   }

	 
	   //@PreAuthorize("hasAuthority('ROLE_RIGHTS_WRITE','ROLES_WRITE')")  
	   public Role updateRole(Role role) {
		   Optional <Role> RoleDb = this.RoleRepo.findById(role.getId());
		    if (RoleDb.isPresent()) {
		        Role RoleUpdate = RoleDb.get();
		        RoleUpdate.setId(role.getId());
		        RoleUpdate.setName(role.getName());
		        RoleUpdate.setDescription(role.getDescription());
		        RoleUpdate.setRights(role.getRights());
		        RoleRepo.save(RoleUpdate);
		        return RoleUpdate;
		    } 
		    else {
		    	throw new ResourceNotFoundException("Resource with the given id :" +role.getId() + " already exists");
		    } 
	   }
	   
      public boolean  deleteRoleById(String id) {

   public boolean delete(String id) {
     Optional <Role> RoleDb = this.RoleRepo.findById(id);

       if (RoleDb.isPresent()) {
           this.RoleRepo.delete(RoleDb.get());

       }      
   	   return RoleDb.isPresent();

       } else {
           throw new ResourceNotFoundException("Record not found with id : " + id);
       }
       return RoleRepo.existsById(id);
 }

    //@PreAuthorize("hasAuthority('ROLE_RIGHTS_READ','ROLES_READ')") 
	public List<Right> getRoleRights(String id) {
		List<Right> rights=new ArrayList<>();
		Role role = getById(id);
		for (Right right : role.getRights()) 
	    		     if (!rights.contains(right)) 
	    		    	 rights.add(right);
		return rights;
	}

	
    //@PreAuthorize("hasAuthority('USER_ROLE_READ')") 
	public List<User> getRoleUsers(String id) {
		   List<User> userslist = new ArrayList<>();
		   List<User> Allusers = userRepo.findAll();
		   for (User user : Allusers) {
			    for (Role role : user.getRoles()) { 
			    		if (role.getId().equals(id)) 
			    		     if (!userslist.contains(user)) 
			    		    	 userslist.add(user);					          
			    }
		   }
		  return userslist;
	}

	
    //@PreAuthorize("hasAuthority('ROLE_RIGHTS_WRITE')") 
	public Role updateRoleRights(Role role) {
		 Optional <Role> RoleDb = this.RoleRepo.findById(role.getId());
		 if (RoleDb.isPresent()) {
		        Role RoleUpdate = RoleDb.get();
		        RoleUpdate.setRights(role.getRights());
		        RoleRepo.save(RoleUpdate);
		        return RoleUpdate;
		    } 
		    else {
		    	throw new ResourceNotFoundException("Resource with the given id :" +role.getId() + " already exists");
		    } 
	}

	// TODO
	// @PreAuthorize("hasAuthority('USER_ROLE_WRITE')") 
	public  List<User> updateRoleUsers(String id,String[] Uids) {
	// TODO Auto-generated method stub
		List<User> userslist = new ArrayList<User> ();
		List<User> Allusers = userRepo.findAll();
		Role role=getRoleById(id);
		List<String> UserIds = List.of(Uids);
		for(User user : Allusers) {
		    if (UserIds.contains(user.getId())) 
	     	{
	   			if(AddRole(role,user))
   			       if (!userslist.contains(user)) 
   			    	   userslist.add(user);	 
			}
		    else {
	   		   	  RemoveRole(role,user);
	   		     // userslist.add(user);
	   		}
	   	}
	    return userslist;
	}
	public boolean AddRole(Role role,User user) {
		  Optional <User> UserDB = this.userRepo.findById(user.getId());
		  List<Role> roles = user.getRoles();
		   if (!(roles.contains(role))) {
		     roles.add(role);
		     User UserRoleUpdate = UserDB.get();
		     UserRoleUpdate.setRoles(roles);
		     userRepo.save(UserRoleUpdate);
		     return true;
	      }  	
	  	  else
	      return false;
	}
	public void RemoveRole(Role role,User user) {
		  Optional <User> UserDB = this.userRepo.findById(user.getId());
		  List<Role> roles = user.getRoles();
		  if (roles.contains(role)) {
			 roles.remove(role);
		     User UserRoleUpdate = UserDB.get();
		     UserRoleUpdate.setRoles(roles);
		     userRepo.save(UserRoleUpdate);			     
	     }  	
	     
	}

}





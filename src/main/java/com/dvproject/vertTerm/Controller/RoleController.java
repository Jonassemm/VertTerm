package com.dvproject.vertTerm.Controller;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.dvproject.vertTerm.Model.ResourceType;

import com.dvproject.vertTerm.Model.Right;
import com.dvproject.vertTerm.Model.Role;
import com.dvproject.vertTerm.Model.User;
import com.dvproject.vertTerm.Service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value= "/Role")
public class RoleController
{
	@Autowired
    private RoleService roleService;  
	
	 @GetMapping
     public  @ResponseBody  List<Role> getAllRole() {
		 return roleService.getAll();
	 }
	
	 @GetMapping("/{id}")
	 public  @ResponseBody  Role getRoleById(@PathVariable String id) {
	     return roleService.getById(id);
	 }
		
	 @GetMapping("/")
	 public List<Role> getRoles(@RequestParam String [] ids) {
			return roleService.getRoles(ids);
		}
	    
	 @GetMapping("/rights/{id}") 
     public  @ResponseBody  List<Right> getRoleRights(@PathVariable String id) {
		 return roleService.getRoleRights(id);
	 }
	 
	 @GetMapping("/users/{id}") 
     public  @ResponseBody  List<User> getRoleUsers(@PathVariable String id) {
		 return roleService.getRoleUsers(id);
	 }
	
	 @PutMapping("/rights/{id}") 
     public  @ResponseBody  List<Right> updateRoleRights(@PathVariable String id, @RequestParam String[] Rids) {	
		 return roleService.updateRoleRights(id,Rids);
	 }
	
	 //TODO es gibt einen Bug
	 @PutMapping("/users/{id}") 
     public  @ResponseBody List<User> updateRoleUsers(@PathVariable String id, @RequestParam String[] Uids) {
		 return roleService.updateRoleUsers(id,Uids);
	 }
	 
	 @PostMapping()
	 public Role createRole(@RequestBody Role role) {
	     return roleService.create(role);
     }

	 @PutMapping("/{id}")
	 public  Role updateRole(@PathVariable String id, @RequestBody Role role) {
	      role.setId(id);
	      return roleService.update(role);
	 }
    
	 @DeleteMapping("/{id}")

	 public boolean deleteRole(@PathVariable String id) {
	     return  roleService.delete(id);
  }

	 
}
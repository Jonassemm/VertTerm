package com.dvproject.vertTerm.Controller;

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
	
	 @GetMapping()
     public  @ResponseBody  List<Role> getAllRole() {
		 return roleService.getAll();
	 }
	
	 @GetMapping("/{id}")
	 public  @ResponseBody  Role getRoleById(@PathVariable String id) {
	     return roleService.getById(id);
	 }
    
	 @GetMapping("/rights/{id}") 
     public  @ResponseBody  List<Right> getRoleRights(@PathVariable String id) {
		 return roleService.getRoleRights(id);
	 }
	 
	 @GetMapping("/users/{id}") 
     public  @ResponseBody  List<User> getRoleUsers(@PathVariable String id) {
		 return roleService.getRoleUsers(id);
	 }
	
	 @PutMapping("/rights/edit/{id}") 
     public  @ResponseBody  Role updateRoleRights(@PathVariable String id, @RequestBody Role role) {
		 role.setId(id);
		 return roleService.updateRoleRights(role);
	 }
	
	 //TODO
	//	 @GetMapping("/users/edit/{id}") 
	//     public  @ResponseBody  updateRoleUsers(@PathVariable String id) {
	//		 return roleService.updateRoleUsers(id);
	//	 }
	 
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
	 public void deleteRole(@PathVariable String id) {
	       roleService.delete(id);
	  }

	 
}
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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.dvproject.vertTerm.Model.Role;
import com.dvproject.vertTerm.Service.RoleService;

@RestController
@RequestMapping(value= "/Api/Role")
public class RoleController
{
	@Autowired
    private RoleService roleService;  
	
	@GetMapping()
     public  @ResponseBody  List<Role> getAllRole() {
		 return roleService.getAllRoles();
	 }
	
	 @GetMapping("/{id}")
	 public  @ResponseBody  Role getRoleById(@PathVariable String id) {
	     return roleService.getRoleById(id);
	 }
    
	 @PostMapping()
	 public Role createRole(@RequestBody Role role) {
	     return roleService.createRole(role);
     }

	 @PutMapping("/{id}")
	 public  Role updateProduct(@PathVariable String id, @RequestBody Role role) {
	      role.setId(id);
	      return roleService.updateRole(role);
	 }
    
	 @DeleteMapping("/{id}")
	 public void deleteProduct(@PathVariable String id) {
	       roleService.deleteRoleById(id);
	  }

}
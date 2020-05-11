package com.dvproject.vertTerm.Controller;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.dvproject.vertTerm.Model.Resource;
import com.dvproject.vertTerm.Model.Role;
import com.dvproject.vertTerm.Service.ResourceService;

@RestController
@RequestMapping(value= "/api/Resource")
public class RessourceController {

	
	 @Autowired
	 private ResourceService resservice;

	 @GetMapping()
	 public List<Resource> getAllResources() {
	    return resservice.getAllResources();
	 }
	
	 @GetMapping("/{id}")
	 public  @ResponseBody  Resource getResourceById(@PathVariable String id) {
	     return resservice.getResourceById(id);
	 }
	 
	 @PostMapping()
	 public Resource createResource(@RequestBody Resource res) {
	     return resservice.createResource(res);
     }
    

	@PutMapping("{id}")
    public Resource  updateResource(@PathVariable String id,@RequestBody Resource res) {
	     res.setId(id);
		 return resservice.updateResource(res);
	 }
	 //   TODO
	//	 @PutMapping("/ava/{id}")
	//		 public Resource  updateResourceAvailability(@RequestBody Resource res) {
	//		 return resservice.updateResourceAvailability(res);
	//	 }
			 
	//	 @PutMapping("/emp/{id}")
	//	 public   updateEmployeePermission() {
	//
	//	 }

	 @DeleteMapping("/{id}")
	 public void deleteResource(@PathVariable String id) {
		 resservice.deleteResourceById(id);
	  }

}
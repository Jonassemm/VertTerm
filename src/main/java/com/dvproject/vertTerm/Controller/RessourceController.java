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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.dvproject.vertTerm.Model.Resource;
import com.dvproject.vertTerm.Model.Restriction;
import com.dvproject.vertTerm.Model.Role;
import com.dvproject.vertTerm.Model.Resource;
import com.dvproject.vertTerm.Service.ResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value= "/Resource")
public class RessourceController {

	
	 @Autowired
	 private ResourceService resservice;

	 @GetMapping()
	 public List<Resource> getAllResources() {
	    return resservice.getAll();
	 }
	
	 @GetMapping("/{id}")
	 public  @ResponseBody  Resource getResourceById(@PathVariable String id) {
	     return resservice.getById(id);
	 }
	 
	 @GetMapping("/res")
	 public  @ResponseBody  List<Resource> getResources(@RequestParam String[] ids) {
	     return resservice.getResources(ids);
	 }
	 
	 @GetMapping("/dep/{id}")
	 public @ResponseBody List<Restriction> getResourceDependencies(@PathVariable String id){
	    return resservice.getResourceDependencies(id);
	 }
	 @PutMapping("/dep/{id}")
	 public  Resource updateResourceDependencies(@PathVariable String id,@RequestBody Resource res) {
	     res.setId(id);
	     return resservice.updateResourceDependencies(res);
	 }
	 
	 @PostMapping()
	 public Resource createResource(@RequestBody Resource res) {
	     return resservice.create(res);
     }
    
	@PutMapping("{id}")
    public Resource  updateResource(@PathVariable String id,@RequestBody Resource res) {
	     res.setId(id);
		 return resservice.update(res);
	 }
	   
		 @PutMapping("/ava/{id}")
			 public Resource  updateResourceAvailability(@PathVariable String id,@RequestBody Resource res) {
			 res.setId(id);
			 return resservice.updateResourceAvailability(res);
		 }
			 
	//	 @PutMapping("/emp/{id}")
	//	 public   updateEmployeePermission() {
	//
	//	 }

	 @DeleteMapping("/{id}")
	 public boolean deleteResource(@PathVariable String id) {
		return resservice.delete(id);

  }

}
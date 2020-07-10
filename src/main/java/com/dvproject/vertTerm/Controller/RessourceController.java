package com.dvproject.vertTerm.Controller;

import java.util.Date;
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

import com.dvproject.vertTerm.Model.Availability;
import com.dvproject.vertTerm.Model.Resource;
import com.dvproject.vertTerm.Model.ResourceType;
import com.dvproject.vertTerm.Model.Restriction;
import com.dvproject.vertTerm.Model.Role;
import com.dvproject.vertTerm.Model.Status;
import com.dvproject.vertTerm.Model.Resource;
import com.dvproject.vertTerm.Service.ResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//author Amar Alkhankan
@RestController
@RequestMapping(value= "/Resources")
public class RessourceController {
	
	 @Autowired
	 private ResourceService resservice;

	 @GetMapping
	 public List<Resource> getAllResources() {
	    return resservice.getAll();
	 }
	
	 @GetMapping("/{id}")
	 public  @ResponseBody  Resource getResourceById(@PathVariable String id) {
	     return resservice.getById(id);
	 }
	 
	 @GetMapping("/")
	 public  @ResponseBody  List<Resource> getResources(@RequestParam String[] ids) {
	     return resservice.getResources(ids);
	 }
	 @GetMapping("/status")
	 public  @ResponseBody  List<Resource> getResources(@RequestParam Status status) {
	     return resservice.getResources(status);
	 }
	 @GetMapping("/restyps/{id}")
	 public  @ResponseBody  List<ResourceType> getResourceTypes(@PathVariable String id) {
	     return resservice.getResourceTypes(id);
	 }
	 
	 @GetMapping("/restyp/{id}")
	 public  @ResponseBody  List<Resource> getResources(@PathVariable String id) {
	     return resservice.getResources(id);
	 }
	 
	 @GetMapping("/ResbyRTandStatus")
	 public  @ResponseBody  List<Resource> getResourcesbyResourceTypeandStatus(@RequestParam String RTid,@RequestParam Status status) {		
		 return resservice.getResourcesbyResourceTypeandStatus(RTid, status);
	 }
	 
	 @PostMapping()
	 public Resource createResource(@RequestBody Resource res) {
	     return resservice.create(res);
     }
    
	@PutMapping("/{id}")
    public Resource  updateResource(@PathVariable String id,@RequestBody Resource res) {
	     res.setId(id);
		 return resservice.update(res);
	 }
	
	@GetMapping("/isAvailable/{id}")
	public boolean isAvailable (@PathVariable String id,@RequestParam Date startdate,@RequestParam Date enddate) {
		return resservice.isResourceAvailableBetween(id, startdate, enddate);
	}
	
	@GetMapping("/Availability/{id}")
	public List<Availability> getResourceAvailability (@PathVariable String id) {
		return resservice.getAllAvailabilities(id);
	}
	
	 @GetMapping("/Restriction/{id}")
	 public @ResponseBody List<Restriction> getResourceRestrictions(@PathVariable String id){
	    return resservice.getResourceRestrictions(id);
	 }
	 
	 @PutMapping("/Restriction/{id}")
	 public  List<Restriction> updateResourceRestrictions(@PathVariable String id,@RequestParam String[] Rids) {
	 
	     return resservice.updateResourceRestrictions(id,Rids);
	 }
	 @PutMapping("/Availability/{id}")
	 public  List<Availability> updateResourceRestrictions(@PathVariable String id,@RequestBody List<Availability> availabilities) {
	     return resservice.updateResourceAvailabilities(id, availabilities);
	 }
	 	 
	 @DeleteMapping("/{id}")
	 public boolean deleteResource(@PathVariable String id) {
		return resservice.delete(id);

  }

}
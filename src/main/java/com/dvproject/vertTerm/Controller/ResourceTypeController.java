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

import com.dvproject.vertTerm.Model.ResourceType;
import com.dvproject.vertTerm.Service.ResourceTypeService;

@RestController
@RequestMapping(value= "/ResourceTypes")
public class ResourceTypeController
{
	@Autowired
    private ResourceTypeService restypService;  
	
	 @GetMapping
     public  @ResponseBody  List<ResourceType> getAllResourceType() {
		 return restypService.getAll();
	 }
	
	 @GetMapping("/{id}")
	 public  @ResponseBody  ResourceType getResourceTypeById(@PathVariable String id) {
	     return restypService.getById(id);
	 }
	
	@GetMapping("/")
     private List<ResourceType> getResourceTypes(@RequestParam String [] ids) {
		return restypService.getResourceTypes(ids);
	}
    	
	 @PostMapping()
	 public ResourceType createResourceType(@RequestBody ResourceType restype) {
	     return restypService.create(restype);
     }

	 @PutMapping("/{id}")
	 public  ResourceType updateResourceType(@PathVariable String id, @RequestBody ResourceType restype) {
		 restype.setId(id);
	      return restypService.update(restype);
	 }
    
	 @DeleteMapping("/{id}")
	 public boolean deleteResourceType(@PathVariable String id) {
		return restypService.delete(id);
	  }

}
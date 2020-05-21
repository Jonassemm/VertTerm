package com.dvproject.vertTerm.Controller;
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
	 
	 @PostMapping()
	 public Resource createResource(@RequestBody Resource res) {
	     return resservice.create(res);
     }
    

	@PutMapping("{id}")
    public Resource  updateResource(@PathVariable String id,@RequestBody Resource res) {
	     res.setId(id);
		 return resservice.update(res);
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
		 resservice.delete(id);
	  }

}
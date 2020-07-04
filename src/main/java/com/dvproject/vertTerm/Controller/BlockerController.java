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

import com.dvproject.vertTerm.Model.Appointment;
import com.dvproject.vertTerm.Model.Blocker;
import com.dvproject.vertTerm.Model.User;
import com.dvproject.vertTerm.Service.BlockerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//author Amar Alkhankan
@RestController
@RequestMapping(value= "/Blocker")
public class BlockerController
{
	@Autowired
    private BlockerService blockerService;  
	
	 @GetMapping
     public  @ResponseBody  List<Blocker> getAllBlocker() {
		 return blockerService.getAll();
	 }
	
	 @GetMapping("/{id}")
	 public  @ResponseBody  Blocker getBlockerById(@PathVariable String id) {
	     return blockerService.getById(id);
	 }
		
	 @GetMapping("/")
	 public List<Blocker> getBlockers(@RequestParam String [] ids) {
			return blockerService.getBlockers(ids);
		}
	 
//	 @GetMapping("/Setflag/{id}")
//	 public List<Appointment> SetWarningFlag(@PathVariable String id) {
//			return blockerService.SetWarningFlag(id);
//		}  
	 
	 @PostMapping()
	 public Blocker createBlocker(@RequestBody Blocker blocker) {
	     return blockerService.create(blocker);
     }

	 @PutMapping("/{id}")
	 public  Blocker updateBlocker(@PathVariable String id, @RequestBody Blocker blocker) {
	      blocker.setId(id);
	      return blockerService.update(blocker);
	 }
    
	 @DeleteMapping("/{id}")
	 public boolean deleteBlocker(@PathVariable String id) {
	     return  blockerService.delete(id);
  }

	 
}
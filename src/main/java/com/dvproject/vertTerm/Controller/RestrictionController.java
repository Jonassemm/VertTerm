package com.dvproject.vertTerm.Controller;

import java.util.ArrayList;
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
import com.dvproject.vertTerm.Service.RestrictionService;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value= "/Restriction")
public class RestrictionController {
	
	 @Autowired
	 private RestrictionService RestService;

	 @GetMapping
	 public List<Restriction> getAllRestrictions() {
	    return RestService.getAll();
	 }
	
	 @GetMapping("/{id}")
	 public  @ResponseBody  Restriction getRestrictionById(@PathVariable String id) {
	     return RestService.getById(id);
	 }
	 
	 @GetMapping("/")
	 public  @ResponseBody  List<Restriction> getRestrictions(@RequestParam String[] ids) {
	     return RestService.getRestrictions(ids);
	 }
	
	 @GetMapping("/testRes")
	 public  @ResponseBody  boolean testRestrictions(@RequestParam  List<Restriction> L1, @RequestParam List<Restriction> L2) {
	     return RestService.testRestrictions(L1, L2);
	 }

	 
	 @PostMapping
	 public Restriction createRestriction(@RequestBody Restriction rest) {
	     return RestService.create(rest);
     }
	 
    
	@PutMapping
    public Restriction updateRestriction(@RequestBody Restriction rest) {	   
		 return RestService.update(rest);
	 }
	   	 	 

	 @DeleteMapping("/{id}")
	 public boolean deleteRestriction(@PathVariable String id) {
		return RestService.delete(id);

  }

}
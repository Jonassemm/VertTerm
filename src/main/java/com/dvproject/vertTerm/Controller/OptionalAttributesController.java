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

import com.dvproject.vertTerm.Model.OptionalAttributes;
import com.dvproject.vertTerm.Service.OptionalAttributesService;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value= "/OptionalAttribute")
public class OptionalAttributesController {
	
	 @Autowired
	 private OptionalAttributesService OpAttsService;

	 @GetMapping
	 public List<OptionalAttributes> getAllOptionalAttributes() {
	    return OpAttsService.getAll();
	 }
	
	 @GetMapping("/{id}")
	 public  @ResponseBody  OptionalAttributes getOptionalAttributesById(@PathVariable String id) {
	     return OpAttsService.getById(id);
	 }
	 
	 
	 @PostMapping
	 public OptionalAttributes createOptionalAttributes(@RequestBody OptionalAttributes opatts) {
	     return OpAttsService.create(opatts);
     }
    
	@PutMapping
    public OptionalAttributes  updateOptionalAttributes(@RequestBody OptionalAttributes opatts) {	   
		return OpAttsService.update(opatts);
	 }
	   	 	 

	 @DeleteMapping("/{id}")
	 public boolean deleteOptionalAttributes(@PathVariable String id) {
		return OpAttsService.delete(id);

  }

}
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

import com.dvproject.vertTerm.Model.OptionalAttribute;
import com.dvproject.vertTerm.Model.OptionalAttributes;
import com.dvproject.vertTerm.Model.Role;
import com.dvproject.vertTerm.Service.OptionalAttributesService;

import org.springframework.web.bind.annotation.*;

import java.util.List;


/** author Amar Alkhankan **/
@RestController
@RequestMapping(value= "/OptionalAttributes")
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
	 
	 @GetMapping("/")
	 public List<OptionalAttributes> getOptionalAttributeswithIDS(@RequestParam String [] ids) {
			return OpAttsService.getOptionalAttributeswithIDS(ids);
	}
	 
	  @GetMapping("/OAs/{id}")
		 public List<OptionalAttribute> getOptionalAttributes(@PathVariable String id) {
			return OpAttsService.getOptionalAttributes(id);
		 }
		 
	 @PostMapping
	 public OptionalAttributes createOptionalAttributes(@RequestBody OptionalAttributes opatts) {
			return OpAttsService.create(opatts);
     }

	@PutMapping("/{id}")
    public OptionalAttributes updateOptionalAttributes(@PathVariable String id,@RequestBody OptionalAttributes opatts) {	 
		opatts.setId(id);
		return OpAttsService.update(opatts);
	 }
	
	 @PutMapping("/AddOA/{id}")
	 public List<OptionalAttribute> AddOptionalAttribute(@PathVariable String id,@RequestBody OptionalAttribute opatt) {
			return OpAttsService.addOptionalAttribute(id, opatt);
     }	 
	    
	 @PutMapping("/EditOA/{id}")
	 public List<OptionalAttribute> EditOptionalAttribute(@PathVariable String id,@RequestBody List<OptionalAttribute> opattList) {
		return OpAttsService.updateOptionalAttribute(id, opattList);
	 }
	 
	 @PutMapping("/DeleteOA/{id}")
	 public List<OptionalAttribute> DeleteOptionalAttribute(@PathVariable String id,@RequestBody OptionalAttribute opatt) {
		return OpAttsService.deleteOptionalAttribute(id,opatt);
	 }
	 @DeleteMapping("/{id}")
	 public boolean deleteOptionalAttributes(@PathVariable String id) {
		return OpAttsService.delete(id);

}

}
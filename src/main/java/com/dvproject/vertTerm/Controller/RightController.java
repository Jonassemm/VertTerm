package com.dvproject.vertTerm.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.dvproject.vertTerm.Model.Right;
import com.dvproject.vertTerm.Model.Role;
import com.dvproject.vertTerm.Service.RightService1;

@RestController
@RequestMapping(value= "/Api/Right")
public class RightController
{
	@Autowired
	private RightService1 rightService;
	
	@GetMapping()
	public List<Right> getAllRights() {
	    return rightService.getAllRights();
	}
	 @GetMapping("/{id}")
	 public  @ResponseBody  Right getRightById(@PathVariable String id) {
	     return rightService.getRightById(id);
	 }
}
package com.dvproject.vertTerm.Controller;

import com.dvproject.vertTerm.Model.Right;
import com.dvproject.vertTerm.Model.Role;
import com.dvproject.vertTerm.Model.User;
import com.dvproject.vertTerm.Service.RightService;
import com.dvproject.vertTerm.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//author Amar Alkhankan
@RestController
@RequestMapping(value= "/Rights")
public class RightController
{
	
	@Autowired
	private RightService rightService;
	@Autowired
	private UserService  userService; 
	@GetMapping
	public List<Right> getAllRights() {
	    return rightService.getAll();
	}
	 @GetMapping("/{id}")
	 public  @ResponseBody  Right getRightById(@PathVariable String id) {
	     return rightService.getById(id);
	 }
	 @GetMapping("/users/{id}")
	 public  List<User> getListUserswithRight(@PathVariable String id) {
		   return rightService.getListUserswithRight(id);
	 }
	 @GetMapping("/roles/{id}")
	 public  List<Role> getLisRoleswithRight(@PathVariable String id) {
		   return rightService.getListRoleswithRight(id);
	 }
}
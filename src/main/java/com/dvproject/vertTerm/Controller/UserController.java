package com.dvproject.vertTerm.Controller;

import com.dvproject.vertTerm.Model.Employee;
import com.dvproject.vertTerm.Model.Status;
import com.dvproject.vertTerm.Model.User;
import com.dvproject.vertTerm.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Null;
import java.util.List;


@RestController
@RequestMapping("/Users")
public class UserController
{
	@Autowired
	private UserService service;

	@GetMapping()
	public @ResponseBody
	List<User> getUsers(@RequestParam(value = "status", required = false) Status status)
	{
		if(status == null){
			return service.getAll();
		}
		else
			return service.getAll(status);
	}

	@GetMapping("/{id}")
	public @ResponseBody
	User getUser(@PathVariable String id)
	{
		return service.getById(id);
	}
}
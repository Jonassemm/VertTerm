package com.dvproject.vertTerm.Controller;

import com.dvproject.vertTerm.Model.User;
import com.dvproject.vertTerm.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/User")
public class UserController
{
	@Autowired
	private UserService service;

	@GetMapping()
	public @ResponseBody
	List<User> getUsers()
	{
		return service.getAllUsers();
	}

	@GetMapping("/{id}")
	public @ResponseBody
	User getUser(@PathVariable String id)
	{
		return service.getUserWithId(id);
	}
}
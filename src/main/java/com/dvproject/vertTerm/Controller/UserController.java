package com.dvproject.vertTerm.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserController
{

	@GetMapping("/users")
	public String getAllUsers(Model model)
	{
		return "userListDisplay";
	}
	
	@GetMapping(value = "/login")
	public String loginUser(Model model)
	{
		return "login";
	}
}
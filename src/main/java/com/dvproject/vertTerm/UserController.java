package com.dvproject.vertTerm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class UserController
{
	@Autowired
	UserManager manager;

	@GetMapping("/users")
	public String getAllUsers(Model model)
	{
		model.addAttribute("users", manager.getAllUsers());
		return "userListDisplay";
	}
}
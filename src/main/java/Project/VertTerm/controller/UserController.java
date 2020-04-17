package Project.VertTerm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import Project.VertTerm.service.UserManager;

@Controller
@RequestMapping("/user-module")
public class UserController
{
	@Autowired
	UserManager manager;

	@RequestMapping(value = "/getAllUsers", method = RequestMethod.GET)
	public String getAllUsers(Model model)
	{
		model.addAttribute("users", manager.getAllUsers());
		return "usersListDisplay";
	}
}
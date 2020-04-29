package com.dvproject.vertTerm.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dvproject.vertTerm.Model.Right;
import com.dvproject.vertTerm.Model.Role;
import com.dvproject.vertTerm.Model.User;
import com.dvproject.vertTerm.Service.UserService;

@RestController
@RequestMapping("/json/user")
public class UserController
{
    @Autowired
    private UserService userService;

    @GetMapping(value = "/")
    public List<User> getAllUsers()
    {
	return userService.getAllUsers();
    }

    @GetMapping("/username")
    public List<User> getUsersWithUsernames(
	    @RequestParam(required = true, defaultValue = "", value = "username") String[] usernames)
    {
	return userService.getUsersWithUsernames(usernames);
    }

    @GetMapping("/id")
    public List<User> getUsersWithIds(
	    @RequestParam(required = true, defaultValue = "", value = "id") String[] ids)
    {
	return userService.getUsersWithIds(ids);
    }

    @GetMapping("/loggedInUser")
    public User getLoggedInUserData()
    {
	return userService.getOwnUser();
    }

    @GetMapping("/id/rights")
    public List<Right> getUserRightsWithId(
	    @RequestParam(required = true, defaultValue = "", value = "id") String id)
    {
	return userService.getUserRightsWithId(id);
    }

    @GetMapping("/id/roles")
    public List<Role> getUserRolesWithId(
	    @RequestParam(required = true, defaultValue = "", value = "id") String id)
    {
	return userService.getUserRolesWithId(id);
    }

}
package com.dvproject.vertTerm.Controller;

import java.util.List;
import java.util.Optional;
import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.dvproject.vertTerm.Model.Right;
import com.dvproject.vertTerm.Model.Role;
import com.dvproject.vertTerm.Model.User;
import com.dvproject.vertTerm.repository.UserRepository;

@RestController
@RequestMapping("/api/User")
public class UserController
{
	@Autowired
	private UserRepository repo;

	@GetMapping()
	public @ResponseBody
	List<User> getUsers()
	{
		return repo.findAll();
	}

	@GetMapping("/{id}")
	public @ResponseBody
	User getUser(@PathVariable String id)
	{
		Optional<User> user = repo.findById(id);
		return user.orElse(null);
	}

	@PostMapping()
	public User CreateUser(@RequestBody User newUser)
	{
		if(!repo.existsById(newUser.getId()))
			return repo.save(newUser);
		else return null;
	}

	@PutMapping("/{id}")
	public User UpdateUser(@RequestBody User newUser, @PathVariable String id)
	{
		Optional<User> oldUser = repo.findById(id);
		if(oldUser.isPresent() && newUser.getId().equals(id)){
			return repo.save(newUser);
		}
		else return null;
	}

	@DeleteMapping("/{id}")
	public boolean DeleteUser(@PathVariable String id)
	{
		repo.deleteById(id);
		return !repo.existsById(id);
	}

	@GetMapping(value = "/login")
	public String loginUser(Model model)
	{
		return "login";
	}
}
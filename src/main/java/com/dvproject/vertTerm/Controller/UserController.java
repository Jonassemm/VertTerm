package com.dvproject.vertTerm.Controller;

import com.dvproject.vertTerm.Model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.dvproject.vertTerm.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/users")
public class UserController
{
	@Autowired
	private UserRepository repo;

	@GetMapping()
	public @ResponseBody
	List<User> getAllUsers()
	{
		return repo.findAll();
	}

	@GetMapping("/{id}")
	public @ResponseBody
	User getUserById(@PathVariable String id)
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
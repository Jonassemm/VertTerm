package com.dvproject.vertTerm.Service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dvproject.vertTerm.Model.Right;
import com.dvproject.vertTerm.Model.Role;
import com.dvproject.vertTerm.Model.User;
import com.dvproject.vertTerm.repository.UserRepository;

@Service
public class UserService
{
    @Autowired
    private UserRepository userRepository;
    
    public List<User> getAllUsers ()
    {
	List <User> users = userRepository.findAll();
	return obfuscatePassword(users);
    }
    
    public List<User> getUsersWithUsernames (String [] usernames)
    {
	List<User> users = new ArrayList<User> ();
	    
	for (String username : usernames) 
	{
	    users.add(userRepository.findByUsername(username));
	}
	    
	return obfuscatePassword(users);
    }
    
    public List<User> getUsersWithIds (String [] ids) {
	List<User> users = new ArrayList<User> ();
	    
	for (String id : ids) 
	{
	    users.add(userRepository.findById(id).get());
	}
	    
	return obfuscatePassword(users);
    }
    
    public List<Role> getUserRolesWithId (String id)
    {
	User user = getUsersWithIds(new String [] {id}).get(0);
	
	return user.getRoles();
    }
    
    public List<Right> getUserRightsWithId (String id)
    {
	List <Right> rights = new ArrayList<Right> ();
	
	for (Role role : getUserRolesWithId(id))
	{
	    for (Right right : role.getRights())
	    {
		if (! rights.contains(right))
		{
		    rights.add(right);
		}
	    }
	}
	
	return rights;
    }
    
    public List<User> obfuscatePassword (List<User> users) {
	for (User user : users)
	{
	    user = obfuscatePassword(user);
	}
	return users;
    }
    
    public User obfuscatePassword (User user) {
	if (user != null)
	{
	    user.setPassword("");
	}
	
	return user;
    }

}

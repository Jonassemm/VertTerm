package com.dvproject.vertTerm;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserManagerImpl implements UserManager {

	@Autowired
    UserDAO dao;
	
	public List<User> getAllUsers()
	{
		return dao.getAllUsers();
	}
}

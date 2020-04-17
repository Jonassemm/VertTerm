package Project.VertTerm.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import Project.VertTerm.dao.UserDAO;
import Project.VertTerm.model.User;

@Service
public class UserManagerImpl implements UserManager {

	@Autowired
    UserDAO dao;
	
	public List<User> getAllUsers()
	{
		return dao.getAllUsers();
	}
}

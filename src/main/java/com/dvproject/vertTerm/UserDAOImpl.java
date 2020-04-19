package com.dvproject.vertTerm;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

@Repository
public class UserDAOImpl implements UserDAO {

	public List<User> getAllUsers()
	{
		List<User> employees = new ArrayList<User>();
		
		User vo1 = new User();
		vo1.setId(1);
		vo1.setFirstName("Lokesh");
		vo1.setLastName("Gupta");
		employees.add(vo1);
		
		User vo2 = new User();
		vo2.setId(2);
		vo2.setFirstName("Raj");
		vo2.setLastName("Kishore");
		employees.add(vo2);
		
		return employees;
	}
}
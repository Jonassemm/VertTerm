package com.dvproject.vertTerm.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.dvproject.vertTerm.Model.User;

public interface UserRepository extends MongoRepository<User, String> 
{
    	Optional<User> findById (String id);
    	
	List<User> findByLastName(String name);
	
	User findByUsername(String username);
	
	@SuppressWarnings("unchecked")
	User save(User user);

}

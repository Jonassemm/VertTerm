package com.dvproject.vertTerm.repository;

import java.util.List;
import java.util.Optional;

import com.dvproject.vertTerm.Model.Employee;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dvproject.vertTerm.Model.User;
import org.springframework.data.mongodb.repository.Query;

public interface UserRepository extends MongoRepository<User, String>
{

	@Query("{'systemStatus' : 'ACTIVE'}")
	List<User> findAllActive();

	@Query("{'systemStatus' : 'INACTIVE'}")
	List<User> findAllInactive();

	@Query("{'systemStatus' : 'DELETED'}")
	List<User> findAllDeleted();

	Optional<User> findById (String id);
	
	List<User> findByLastName(String lastname);
	
	User findByUsername(String username);
}

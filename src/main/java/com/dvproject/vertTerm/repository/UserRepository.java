package com.dvproject.vertTerm.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.dvproject.vertTerm.Model.User;

import org.springframework.data.mongodb.repository.Query;

public interface UserRepository extends MongoRepository<User, String>
{

	/**
	 * @author Robert Schulz
	 */
	@Query("{'systemStatus' : 'ACTIVE'}")
	List<User> findAllActive();

	/**
	 * @author Robert Schulz
	 */
	@Query("{'systemStatus' : 'INACTIVE'}")
	List<User> findAllInactive();

	/**
	 * @author Robert Schulz
	 */
	@Query("{'systemStatus' : 'DELETED'}")
	List<User> findAllDeleted();

	/**
	 * @author Robert Schulz
	 */
	Optional<User> findById (String id);
	
	/**
	 * @author Joshua Müller
	 */
	List<User> findByLastName(String lastname);
	
	/**
	 * @author Joshua Müller
	 */
	User findByUsername(String username);
}

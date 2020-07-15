package com.dvproject.vertTerm.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.dvproject.vertTerm.Model.Resource;
import com.dvproject.vertTerm.Model.Status;

public interface ResourceRepository extends MongoRepository<Resource, String> {
	/**
	 * @author Joshua Müller
	 */
	Optional<Resource> findById (String id);
	
	Resource findByName(String name);
	List<Resource> findByStatus(Status status);
		
}

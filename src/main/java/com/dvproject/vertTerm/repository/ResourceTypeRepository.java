package com.dvproject.vertTerm.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.dvproject.vertTerm.Model.ResourceType;
import com.dvproject.vertTerm.Model.Status;

/**
 * @author Joshua Müller
 */
public interface ResourceTypeRepository extends MongoRepository<ResourceType, String>{
	/**
	 * @author Joshua Müller
	 */
	Optional<ResourceType> findById(String id);
	
	ResourceType findByName(String name);
	
	/**
	 * @author Joshua Müller
	 */
	List<ResourceType> findByStatus(Status status);
}

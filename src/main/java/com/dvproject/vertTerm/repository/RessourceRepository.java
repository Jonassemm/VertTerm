package com.dvproject.vertTerm.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.dvproject.vertTerm.Model.Resource;
import com.dvproject.vertTerm.Model.ResourceType;
import com.dvproject.vertTerm.Model.Status;

public interface RessourceRepository extends MongoRepository<Resource, String> {
	Optional<Resource> findById (String id);
	Resource findByName(String name);
	List<Resource> findByStatus(Status status);
		
}

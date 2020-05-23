package com.dvproject.vertTerm.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.dvproject.vertTerm.Model.ResourceType;

public interface ResourceTypeRepository extends MongoRepository<ResourceType, String>{
	Optional<ResourceType> findById(String id);
	ResourceType findByName(String name);
}

package com.dvproject.vertTerm.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.dvproject.vertTerm.Model.Resource;

public interface RessourceRepository extends MongoRepository<Resource, String> {
	 Optional<Resource> findById (String id);
	Resource findByName(String name);
}

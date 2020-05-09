package com.dvproject.vertTerm.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.dvproject.vertTerm.Model.Restriction;

public interface RestrictionRepository extends MongoRepository<Restriction, String>{
	Optional<Restriction> findById (String id);
}

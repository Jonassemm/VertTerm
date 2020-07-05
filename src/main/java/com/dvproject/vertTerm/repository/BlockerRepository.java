package com.dvproject.vertTerm.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.dvproject.vertTerm.Model.Blocker;

//author Amar Alkhankan
public interface BlockerRepository extends MongoRepository<Blocker, String>{
	 Optional<Blocker> findById (String id);
	 Blocker findByname (String name);
}
package com.dvproject.vertTerm.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.dvproject.vertTerm.Model.Blocker;

//author Amar Alkhankan
public interface BlockerRepository extends MongoRepository<Blocker, String>{
	 @Query("{'_id' : ?0, '_class' : 'com.dvproject.vertTerm.Model.Blocker'}")
	 Optional<Blocker> findById (String id);
	 Blocker findByname (String name);
}
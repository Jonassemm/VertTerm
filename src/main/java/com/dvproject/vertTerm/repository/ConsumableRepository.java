package com.dvproject.vertTerm.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.dvproject.vertTerm.Model.Consumable;

public interface ConsumableRepository extends MongoRepository<Consumable, String> {
	@Query("{'_id' : ?0, '_class' : 'com.dvproject.vertTerm.Model.Consumable'}")
	Optional<Consumable> findById (String id);
}

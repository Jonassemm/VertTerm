package com.dvproject.vertTerm.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.dvproject.vertTerm.Model.Building;

public interface BuildingRepository extends MongoRepository<Building, String> {
	@Query("{'_id' : ?0, '_class' : 'com.dvproject.vertTerm.Model.Building'}")
	Optional<Building> findById (String id);
}

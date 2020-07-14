package com.dvproject.vertTerm.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.dvproject.vertTerm.Model.Consumable;
import com.dvproject.vertTerm.Model.Status;

/**
 * @author Joshua Müller
 */
public interface ConsumableRepository extends MongoRepository<Consumable, String> {
//	@Query("{'_id' : ?0, '_class' : 'com.dvproject.vertTerm.Model.Consumable'}")
	Optional<Consumable> findById (String id);
	
//	@Query("'_class' : 'com.dvproject.vertTerm.Model.Consumable'}")
//	List<Consumable> findAll ();
	
//	@Query("{'status' : ?0, '_class' : 'com.dvproject.vertTerm.Model.Consumable'}")
	List<Consumable> findByStatus (Status status);
}

package com.dvproject.vertTerm.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.dvproject.vertTerm.Model.OptionalAttributes;


/** author Amar Alkhankan **/
public interface OptionalAttributesRepository extends MongoRepository<OptionalAttributes, String>{

	@Query("{'_id' : ?0, '_class' : 'com.dvproject.vertTerm.Model.OptionalAttributes'}")
	Optional<OptionalAttributes> findById (String id);
	@Query("{'classOfOptionalAttribut' : ?0, '_class' : 'com.dvproject.vertTerm.Model.OptionalAttributes'}")
	OptionalAttributes findByClass(String classOfOptionalAttribut);
	
}

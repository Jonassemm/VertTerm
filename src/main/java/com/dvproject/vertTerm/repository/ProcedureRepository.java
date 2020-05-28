package com.dvproject.vertTerm.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.dvproject.vertTerm.Model.Procedure;

public interface ProcedureRepository extends MongoRepository<Procedure, String> {
	Optional<Procedure> findById (String id);
	
	@Query("{_id: { $in : ?0}}")
	List<Procedure> findByIds (String [] ids);
	
	@Query("{'systemStatus' : 'ACTIVE'}")
	List<Procedure> findAllActive();
	
	@Query("{'systemStatus' : 'INACTIVE'}")
	List<Procedure> findAllInactive();
	
	@Query("{'systemStatus' : 'DELETED'}")
	List<Procedure> findAllDeleted();
}

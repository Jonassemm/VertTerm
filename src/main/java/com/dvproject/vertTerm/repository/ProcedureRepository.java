package com.dvproject.vertTerm.repository;

import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.dvproject.vertTerm.Model.Procedure;
import com.dvproject.vertTerm.Model.Status;

/**
 * @author Joshua Müller
 */
public interface ProcedureRepository extends MongoRepository<Procedure, String> {
	Optional<Procedure> findById (String id);
	
	@Query("{_id: { $in : ?0}}")
	List<Procedure> findByIds (String [] ids);

	List<Procedure> findByStatus(Status status);
	
	List<Procedure> findByStatusAndPublicProcedure(Status status, boolean publicProcedure);
	
	@Query("{'neededResourceTypes.$id' : {$in : ?0}}")
	List<Procedure> findByNeededResourceTypesIdIn(List<ObjectId> ids);
	
	@Query("{'neededEmployeePositions.$id' : {$in : ?0}}")
	List<Procedure> findByNeededEmployeePositionsIn(List<ObjectId> ids);
}

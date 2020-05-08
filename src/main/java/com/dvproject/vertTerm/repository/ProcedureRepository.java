package com.dvproject.vertTerm.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.dvproject.vertTerm.Model.Procedure;

public interface ProcedureRepository extends MongoRepository<Procedure, String> {
	Optional<Procedure> findById (String id);
}

package com.dvproject.vertTerm.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.dvproject.vertTerm.Model.Position;
import com.dvproject.vertTerm.Model.Status;

/**
 * @author Joshua Müller
 */
public interface PositionRepository extends MongoRepository<Position, String>{
    Optional<Position> findById (String id);
    
    List<Position> findByStatus(Status status);
}

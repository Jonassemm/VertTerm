package com.dvproject.vertTerm.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.dvproject.vertTerm.Model.Right;

/**
 * @author Joshua Müller
 */
public interface RightRepository extends MongoRepository<Right, String>{
    Optional<Right> findById(String id);
    
    Right findByName(String name);

    Right save(Right entity);
}
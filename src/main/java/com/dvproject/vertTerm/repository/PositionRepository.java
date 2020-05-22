package com.dvproject.vertTerm.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.dvproject.vertTerm.Model.Position;

public interface PositionRepository extends MongoRepository<Position, String>
{
    Optional<Position> findById (String id);
    
    Position save (Position position);
}

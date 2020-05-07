package com.dvproject.vertTerm.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.dvproject.vertTerm.Model.Room;

public interface RoomRepository extends MongoRepository<Room, String> {
	@Query("{'_id' : ?0, '_class' : 'com.dvproject.vertTerm.Model.Room'}")
	Optional<Room> findById (String id);
}

package com.dvproject.vertTerm.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.dvproject.vertTerm.Model.Availability;

public interface AvailabilityRepository extends MongoRepository<Availability, String>{

}

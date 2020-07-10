package com.dvproject.vertTerm.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.dvproject.vertTerm.Model.OpeningHours;

/**
 * @author Joshua Müller
 */
public interface OpeningHoursRepository extends MongoRepository<OpeningHours, String>{

}

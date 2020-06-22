package com.dvproject.vertTerm.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.dvproject.vertTerm.Model.OpeningHours;

public interface OpeningHoursRepository extends MongoRepository<OpeningHours, String>{

}

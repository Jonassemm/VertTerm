package com.dvproject.vertTerm.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.dvproject.vertTerm.Model.Appointment;

public interface AppointmentRepository extends MongoRepository<Appointment, String> {
	Optional<Appointment> findById (String id);
}

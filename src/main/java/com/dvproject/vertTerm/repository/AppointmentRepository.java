package com.dvproject.vertTerm.repository;

import java.util.List;
import java.util.Optional;

import com.dvproject.vertTerm.Model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.dvproject.vertTerm.Model.Appointment;
import com.dvproject.vertTerm.Model.Status;
import com.dvproject.vertTerm.Model.Warning;

public interface AppointmentRepository extends MongoRepository<Appointment, String> {
	Optional<Appointment> findById (String id);
	
	List<Appointment> findByWarning(Warning warning);
	
	List<Appointment> findByStatus(Status status);
	
	@Query("{'bookedCustomer.id': ?0}")
	List<Appointment> findByBookedCustomerId(String id);
}

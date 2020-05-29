package com.dvproject.vertTerm.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.dvproject.vertTerm.Model.Appointmentgroup;

public interface AppointmentgroupRepository extends MongoRepository<Appointmentgroup, String> {
	Optional<Appointmentgroup> findById(String id);
	
	Appointmentgroup findByAppointmentsId (String id);
}

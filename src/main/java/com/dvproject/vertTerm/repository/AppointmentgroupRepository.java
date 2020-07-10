package com.dvproject.vertTerm.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.dvproject.vertTerm.Model.Appointmentgroup;
import com.dvproject.vertTerm.Model.Status;

/**
 * @author Joshua Müller
 */
public interface AppointmentgroupRepository extends MongoRepository<Appointmentgroup, String> {
	Optional<Appointmentgroup> findById(String id);
	
	Appointmentgroup findByAppointmentsId(String id);
	
	List<Appointmentgroup> findByStatus(Status status);
}

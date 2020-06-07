package com.dvproject.vertTerm.repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import com.dvproject.vertTerm.Model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.dvproject.vertTerm.Model.Appointment;
import com.dvproject.vertTerm.Model.Status;
import com.dvproject.vertTerm.Model.Warning;

public interface AppointmentRepository extends MongoRepository<Appointment, String> {
	Optional<Appointment> findById(String id);

	List<Appointment> findByWarning(Warning warning);

	List<Appointment> findByStatus(Status status);

	@Query("{'bookedCustomer.id': ?0}")
	List<Appointment> findByBookedCustomerId(String id);

	@Query("{'$and':[{'bookedCustomer.$id': ObjectId(?0)}, {'plannedStarttime': {'$gte': ?1}}, {'plannedEndtime': {'$lte': ?2}}]}")
	List<Appointment> findAppointmentByBookedUserAndTime(String userid, Date starttime, Date endtime);
}

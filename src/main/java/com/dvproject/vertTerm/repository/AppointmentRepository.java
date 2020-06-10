package com.dvproject.vertTerm.repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.dvproject.vertTerm.Model.Appointment;
import com.dvproject.vertTerm.Model.AppointmentStatus;
import com.dvproject.vertTerm.Model.Status;
import com.dvproject.vertTerm.Model.Warning;

public interface AppointmentRepository extends MongoRepository<Appointment, String> {
	Optional<Appointment> findById(String id);

	List<Appointment> findByWarnings(Warning warning);

	List<Appointment> findByStatus(Status status);

	@Query("{'bookedCustomer.id': ?0}")
	List<Appointment> findByBookedCustomerId(String id);
	
	@Query("{'$and':[{'plannedStarttime': {'$gte': ?0}}, {'plannedEndtime': {'$lte': ?1}}]}")
	List<Appointment> findAppointmentsByTimeinterval(Date starttime, Date endtime);
	
	@Query("{'$and':[{'plannedStarttime': {'$gte': ?0}}, {'plannedEndtime': {'$lte': ?1}}, {'status': ?2}]}")
	List<Appointment> findAppointmentsByTimeintervalAndStatus(Date starttime, Date endtime, AppointmentStatus status);

	@Query("{'$and':[{'bookedCustomer.$id': ObjectId(?0)}, {'plannedStarttime': {'$gte': ?1}}, {'plannedEndtime': {'$lte': ?2}}]}")
	List<Appointment> findAppointmentsByBookedUserAndTimeinterval(String userid, Date starttime, Date endtime);
	
	@Query("'$and':[{'bookedEmployees.$id': ObjectId(?0)}, {'$or':[{'$and':[{'plannedStarttime' : {'$gte': ?1}}, {'plannedEndtime': {'$lt': ?1}}]}, {'$and':[{'plannedStarttime' : {'$lt': ?2}}, {'plannedEndtime': {'$gte': ?2}}]}, {'$and':[{'plannedStarttime' : {'$gte': ?1}}, {'plannedEndtime': {'$lte': ?2}}]}]}, {'status': ?3}]}")
	List<Appointment> findAppointmentsByBookedEmployeeInTimeinterval(String employeeid, Date starttime, Date endtime, AppointmentStatus status);
	
	@Query("{'$and':[{'bookedResources.$id': ObjectId(?0)}, {'$or':[{'$and':[{'plannedStarttime' : {'$gte': ?1}}, {'plannedEndtime': {'$lt': ?1}}]}, {'$and':[{'plannedStarttime' : {'$lt': ?2}}, {'plannedEndtime': {'$gte': ?2}}]}, {'$and':[{'plannedStarttime' : {'$gte': ?1}}, {'plannedEndtime': {'$lte': ?2}}]}]}, {'status': ?3}]}")
	List<Appointment> findAppointmentsByBookedResourceInTimeinterval(String resourceid, Date starttime, Date endtime, AppointmentStatus status);
	
	@Query("{'$and':[{'bookedCustomer.$id': ObjectId(?0)}, {'$or':[{'$and':[{'plannedStarttime' : {'$gte': ?1}}, {'plannedEndtime': {'$lt': ?1}}]}, {'$and':[{'plannedStarttime' : {'$lt': ?2}}, {'plannedEndtime': {'$gte': ?2}}]}, {'$and':[{'plannedStarttime' : {'$gte': ?1}}, {'plannedEndtime': {'$lte': ?2}}]}]}, {'status': ?3}]}")
	List<Appointment> findAppointmentsByBookedCustomerInTimeinterval(String userid, Date starttime, Date endtime, AppointmentStatus status);
}

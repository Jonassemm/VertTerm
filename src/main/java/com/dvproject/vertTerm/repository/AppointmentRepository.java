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

	List<Appointment> findByWarnings(Warning warnings);
	
	List<Appointment> findByWarningsIn(List<Warning> warnings);
	
	List<Appointment> findByBookedCustomerIdAndWarnings(String userid, Warning warning);
	
	List<Appointment> findByBookedCustomerIdAndWarningsIn(String userid, List<Warning> warnings);

	List<Appointment> findByStatus(Status status);

	List<Appointment> findByBookedCustomerId(String id);
	
	List<Appointment> findByBookedEmployeesId(String id);
	
	List<Appointment> findByBookedResourcesId(String id);
	
	List<Appointment> findByBookedCustomerIdAndStatus(String id, AppointmentStatus status);
	
	List<Appointment> findByBookedEmployeesIdAndStatus(String id, AppointmentStatus status);
	
	List<Appointment> findByBookedResourcesIdAndStatus(String id, AppointmentStatus status);
	
	List<Appointment> findByBookedProcedureIdAndPlannedStarttimeAfter(String id, Date plannedStarttime);
	
	List<Appointment> findByBookedResourcesIdAndPlannedStarttimeAfter(String id, Date plannedStarttime);
	
	List<Appointment> findByBookedEmployeesIdAndPlannedStarttimeAfter(String id, Date plannedStarttime);
	
	@Query("{'$and':[{'plannedStarttime': {'$gte': ?0}}, {'plannedEndtime': {'$lte': ?1}}]}")
	List<Appointment> findAppointmentsByTimeinterval(Date starttime, Date endtime);
	
	@Query("{'$or':[{'$and':[{'plannedStarttime' : {'$lte': ?0}}, {'plannedEndtime': {'$gt': ?0}}]}, {'$and':[{'plannedStarttime' : {'$lt': ?1}}, {'plannedEndtime': {'$gte': ?1}}]}, {'$and':[{'plannedStarttime' : {'$gte': ?0}}, {'plannedEndtime': {'$lte': ?1}}]}], 'status': ?2}")
	List<Appointment> findAllOverlappingAppointmentsWithStatus(Date starttime, Date endtime, AppointmentStatus status);
	
	@Query("{'$and':[{'plannedStarttime': {'$gte': ?0}}, {'plannedEndtime': {'$lte': ?1}}, {'status': ?2}]}")
	List<Appointment> findAppointmentsByTimeintervalAndStatus(Date starttime, Date endtime, AppointmentStatus status);

	@Query("{'$and':[{'bookedCustomer.$id': ObjectId(?0)}, {'plannedStarttime': {'$gte': ?1}}, {'plannedEndtime': {'$lte': ?2}}]}")
	List<Appointment> findAppointmentsByBookedUserAndTimeinterval(String userid, Date starttime, Date endtime);
	
	@Query("{'bookedEmployees.$id': ObjectId(?0), '$or':[{'$and':[{'plannedStarttime' : {'$lte': ?1}}, {'plannedEndtime': {'$gt': ?1}}]}, {'$and':[{'plannedStarttime' : {'$lt': ?2}}, {'plannedEndtime': {'$gte': ?2}}]}, {'$and':[{'plannedStarttime' : {'$gte': ?1}}, {'plannedEndtime': {'$lte': ?2}}]}], 'status': ?3}")
	List<Appointment> findAppointmentsByBookedEmployeeInTimeinterval(String employeeid, Date starttime, Date endtime, AppointmentStatus status);
	
	@Query("{'bookedResources.$id': ObjectId(?0), '$or':[{'$and':[{'plannedStarttime' : {'$lte': ?1}}, {'plannedEndtime': {'$gt': ?1}}]}, {'$and':[{'plannedStarttime' : {'$lt': ?2}}, {'plannedEndtime': {'$gte': ?2}}]}, {'$and':[{'plannedStarttime' : {'$gte': ?1}}, {'plannedEndtime': {'$lte': ?2}}]}], 'status': ?3}")
	List<Appointment> findAppointmentsByBookedResourceInTimeinterval(String resourceid, Date starttime, Date endtime, AppointmentStatus status);
	
	@Query("{'bookedProcedure.$id': ObjectId(?0), '$or':[{'$and':[{'plannedStarttime' : {'$lte': ?1}}, {'plannedEndtime': {'$gt': ?1}}]}, {'$and':[{'plannedStarttime' : {'$lt': ?2}}, {'plannedEndtime': {'$gte': ?2}}]}, {'$and':[{'plannedStarttime' : {'$gte': ?1}}, {'plannedEndtime': {'$lte': ?2}}]}], 'status': ?3}")
	List<Appointment> findAppointmentsByBookedProceudreInTimeinterval(String procedureid, Date starttime, Date endtime, AppointmentStatus status);
	
	@Query("{'bookedCustomer.$id': ObjectId(?0), '$or':[{'$and':[{'plannedStarttime' : {'$lte': ?1}}, {'plannedEndtime': {'$gt': ?1}}]}, {'$and':[{'plannedStarttime' : {'$lt': ?2}}, {'plannedEndtime': {'$gte': ?2}}]}, {'$and':[{'plannedStarttime' : {'$gte': ?1}}, {'plannedEndtime': {'$lte': ?2}}]}], 'status': ?3}")
	List<Appointment> findAppointmentsByBookedCustomerInTimeinterval(String userid, Date starttime, Date endtime, AppointmentStatus status);
}

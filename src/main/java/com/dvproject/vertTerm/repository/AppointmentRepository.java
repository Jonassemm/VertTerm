package com.dvproject.vertTerm.repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.dvproject.vertTerm.Model.Appointment;
import com.dvproject.vertTerm.Model.AppointmentStatus;
import com.dvproject.vertTerm.Model.Status;
import com.dvproject.vertTerm.Model.Warning;

/**
 * @author Joshua Müller
 */
public interface AppointmentRepository extends MongoRepository<Appointment, String> {
	static final String overlapsWithOtherAppointment = "'$or':[{'$and':[{'plannedStarttime' : {'$lte': ?1}}, {'plannedEndtime': {'$gt': ?1}}]}, {'$and':[{'plannedStarttime' : {'$lt': ?2}}, {'plannedEndtime': {'$gte': ?2}}]}, {'$and':[{'plannedStarttime' : {'$gte': ?1}}, {'plannedEndtime': {'$lte': ?2}}]}]";

	Optional<Appointment> findById(String id);

	List<Appointment> findByWarnings(Warning warnings);

	List<Appointment> findByWarningsIn(List<Warning> warnings);

	List<Appointment> findByStatus(Status status);

	// bookedCustomer
	List<Appointment> findByBookedCustomerId(String customerid);

	List<Appointment> findByBookedCustomerIdAndStatus(String customerid, AppointmentStatus status);

	List<Appointment> findByBookedCustomerIdAndWarnings(String customerid, Warning warning);

	List<Appointment> findByBookedCustomerIdAndWarningsIn(String customerid, List<Warning> warnings);

	@Query("{'bookedCustomer.$id': ObjectId(?0), " + overlapsWithOtherAppointment + "}")
	List<Appointment> findAppointmentsByBookedCustomerInTimeinterval(String customerid, Date starttime, Date endtime);

	@Query("{'bookedCustomer.$id': ObjectId(?0), " + overlapsWithOtherAppointment + ", 'status': ?3}")
	List<Appointment> findAppointmentsByBookedCustomerInTimeintervalWithStatus(String customerid, Date starttime,
			Date endtime, AppointmentStatus status);

	// bookedEmployees
	List<Appointment> findByBookedEmployeesId(String employeeid);

	List<Appointment> findByBookedEmployeesIdAndStatus(String employeeid, AppointmentStatus status);

	List<Appointment> findByBookedEmployeesIdAndPlannedStarttimeAfter(String employeeid, Date plannedStarttime);

	@Query("{'bookedEmployees.$id': ObjectId(?0), " + overlapsWithOtherAppointment + "}")
	List<Appointment> findAppointmentsByBookedEmployeeInTimeinterval(String employeeid, Date starttime, Date endtime);

	@Query("{'bookedEmployees.$id': ObjectId(?0), " + overlapsWithOtherAppointment + ", 'status': ?3}")
	List<Appointment> findAppointmentsByBookedEmployeeInTimeintervalWithStatus(String employeeid, Date starttime,
			Date endtime, AppointmentStatus status);

	// bookedResources
	List<Appointment> findByBookedResourcesId(String resourceid);

	List<Appointment> findByBookedResourcesIdAndStatus(String resourceid, AppointmentStatus status);

	List<Appointment> findByBookedResourcesIdAndPlannedStarttimeAfter(String resourceid, Date plannedStarttime);

	@Query("{'bookedResources.$id': ObjectId(?0), " + overlapsWithOtherAppointment + "}")
	List<Appointment> findAppointmentsByBookedResourceInTimeinterval(String resourceid, Date starttime, Date endtime);

	@Query("{'bookedResources.$id': ObjectId(?0), " + overlapsWithOtherAppointment + ", 'status': ?3}")
	List<Appointment> findAppointmentsByBookedResourceInTimeintervalWithStatus(String resourceid, Date starttime,
			Date endtime, AppointmentStatus status);

	// bookedProcedure
	List<Appointment> findByBookedProcedureId(String procedureid);

	List<Appointment> findByBookedProcedureIdAndStatus(String procedureid, AppointmentStatus status);

	List<Appointment> findByBookedProcedureIdAndPlannedStarttimeAfter(String procedureid, Date plannedStarttime);

	@Query("{'bookedProcedure.$id': ObjectId(?0), " + overlapsWithOtherAppointment + "}")
	List<Appointment> findAppointmentsByBookedProceudreInTimeinterval(String procedureid, Date starttime, Date endtime);

	@Query("{'bookedProcedure.$id': ObjectId(?0), " + overlapsWithOtherAppointment + ", 'status': ?3}")
	List<Appointment> findAppointmentsByBookedProceudreInTimeintervalWithStatus(String procedureid, Date starttime,
			Date endtime, AppointmentStatus status);

	// other
	@Query("{'$and':[{'plannedStarttime': {'$gte': ?0}}, {'plannedEndtime': {'$lte': ?1}}]}")
	List<Appointment> findAppointmentsByTimeinterval(Date starttime, Date endtime);

	@Query("{" + overlapsWithOtherAppointment + ", 'status': ?0}")
	List<Appointment> findAllOverlappingAppointmentsWithStatus(AppointmentStatus status, Date starttime, Date endtime);

	@Query("{'$and':[{'plannedStarttime': {'$gte': ?0}}, {'plannedEndtime': {'$lte': ?1}}, {'status': ?2}]}")
	List<Appointment> findAppointmentsByTimeintervalAndStatus(Date starttime, Date endtime, AppointmentStatus status);

	@Query("{'$and':[{'bookedCustomer.$id': ObjectId(?0)}, {'plannedStarttime': {'$gte': ?1}}, {'plannedEndtime': {'$lte': ?2}}]}")
	List<Appointment> findAppointmentsByBookedUserAndTimeinterval(String userid, Date starttime, Date endtime);

	@Query("{'$or': [{'bookedEmployees.$id': {'$in': ?0}}, {'bookedResources.$id': {'$in': ?1}}], 'plannedStarttime': {'$gt' : ?2}, 'status': ?3}")
	List<Appointment> findAppointmentsWithCustomerEmployeeAndResourceAfterPlannedStarttime(List<ObjectId> employeeid,
			List<ObjectId> resourceid, Date starttime, AppointmentStatus status);

}

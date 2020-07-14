package com.dvproject.vertTerm.Service;

import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;

import com.dvproject.vertTerm.Model.*;

/**
 * @author Joshua Müller
 */
public interface AppointmentService extends BasicService<Appointment> {
	// GET
	List<Appointment> getAll(Bookable bookable);

	/** author Amar Alkhankan **/
	// Buchungoption2 get available Resources and Employees
	public Appointmentgroup getAvailableResourcesAndEmployees(Appointmentgroup group);

	// appointments with warning
	List<Appointment> getAllAppointmentsByUseridAndWarnings(String userid, List<Warning> warnings);

	// appointments of user/employee/resource
	List<Appointment> getAppointmentsByUserIdAndAppointmentStatus(String userid, AppointmentStatus status);

	List<Appointment> getAppointmentsByEmployeeIdAndAppointmentStatus(String employeeid, AppointmentStatus status);

	List<Appointment> getAppointmentsByResourceIdAndAppointmentStatus(String resourceid, AppointmentStatus status);

	List<Appointment> getAppointmentsByProcedureIdAndAppointmentStatus(String procedureid, AppointmentStatus status);

	// appointments of an available-entity in a time interval
	List<Appointment> getAppointmentsOfBookedEmployeeInTimeinterval(String employeeid, Date starttime, Date endtime,
			AppointmentStatus status);

	List<Appointment> getAppointmentsOfBookedResourceInTimeinterval(String resourceid, Date starttime, Date endtime,
			AppointmentStatus status);

	List<Appointment> getAppointmentsOfBookedProcedureInTimeinterval(String procedureid, Date starttime, Date endtime,
			AppointmentStatus status);

	List<Appointment> getAppointmentsOfBookedCustomerInTimeinterval(String userid, Date starttime, Date endtime,
			AppointmentStatus status);

	List<Appointment> getAppointmentsWithUseridAndTimeInterval(String userid, Date starttime, Date endtime);

	List<Appointment> getAppointmentsWithCustomerEmployeeResourceAfterDate(List<ObjectId> employeeids,
			List<ObjectId> resourceids, Date startdate, AppointmentStatus status);

	// appointment in time interval
	List<Appointment> getAppointmentsInTimeIntervalAndStatus(Date starttime, Date endtime, AppointmentStatus status);

	List<Appointment> getOverlappingAppointmentsInTimeInterval(Date starttime, Date endtime, AppointmentStatus status);

	// appointments of an available-entity
	List<Appointment> getAppointments(Available available, Date startdate);

	List<Appointment> getAppointmentsOf(Employee employee, Date startdate);

	List<Appointment> getAppointmentsOf(Procedure procedure, Date startdate);

	List<Appointment> getAppointmentsOf(Resource resource, Date startdate);
	
	void loadAppointment(Appointment appointment);

	// PUT
	boolean setCustomerIsWaiting(String id, boolean customerIsWaiting);
	
	List<Appointment> cleanseAppointmentsOfBlocker(List<Appointment> appointments);
}

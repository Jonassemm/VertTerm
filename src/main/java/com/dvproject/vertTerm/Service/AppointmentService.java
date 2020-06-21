package com.dvproject.vertTerm.Service;

import java.util.Date;
import java.util.List;

import com.dvproject.vertTerm.Model.Appointment;
import com.dvproject.vertTerm.Model.AppointmentStatus;
import com.dvproject.vertTerm.Model.Appointmentgroup;
import com.dvproject.vertTerm.Model.Available;
import com.dvproject.vertTerm.Model.Bookable;
import com.dvproject.vertTerm.Model.Res_Emp;
import com.dvproject.vertTerm.Model.Warning;

public interface AppointmentService extends BasicService<Appointment> {
	// GET
	List<Appointment> getAll(Bookable bookable);
	//Available Resources and Employees
	public Res_Emp getAvailableResourcesAndEmployees(Appointmentgroup group);
	// appointments with warning
	List<Appointment> getAppointmentsByWarning(Warning warning);
	
	List<Appointment> getAppointmentsByWarnings(List<Warning> warnings);
	
	List<Appointment> getAppointmentsByWarningAndId(String userid, Warning warning);
	
	List<Appointment> getAppointmentsByWarningsAndId(String userid, List<Warning> warnings);

	// appointments of user
	List<Appointment> getAppointmentsByUserid(String userid);

	List<Appointment> getAppointmentsByUserid(String userid, AppointmentStatus appointmentStatus);
	
	List<Appointment> getAppointmentsByEmployeeid(String employeeid);

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

	// appointment in time interval
	List<Appointment> getAppointmentsInTimeIntervalWithStatus(Date starttime, Date endtime, AppointmentStatus status);

	List<Appointment> getAppointmentsInTimeInterval(Date starttime, Date endtime);

	// appointments of an available-entity
	List<Appointment> getAppointments(Available available, Date endOfSeries);

	List<Appointment> getAppointmentsOfEmployee(String employeeid, Date startdate);

	List<Appointment> getAppointmentsOfProcedure(String procedureid, Date startdate);

	List<Appointment> getAppointmentsOfResource(String resourceid, Date startdate);

	// PUT
	boolean setCustomerIsWaiting(String id, boolean customerIsWaiting);
}

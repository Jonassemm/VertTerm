package com.dvproject.vertTerm.Service;

import java.util.Date;
import java.util.List;

import com.dvproject.vertTerm.Model.Appointment;
import com.dvproject.vertTerm.Model.AppointmentStatus;
import com.dvproject.vertTerm.Model.Bookable;

public interface AppointmentService extends BasicService<Appointment> {
	// GET
	List<Appointment> getAll(Bookable bookable);

	List<Appointment> getAppointmentsByUserid(String userid);

	List<Appointment> getAppointmentsOfBookedEmployeeInTimeinterval(String employeeid, Date starttime, Date endtime,
			AppointmentStatus status);

	List<Appointment> getAppointmentsOfBookedResourceInTimeinterval(String resourceid, Date starttime, Date endtime,
			AppointmentStatus status);

	List<Appointment> getAppointmentsOfBookedCustomerInTimeinterval(String userid, Date starttime, Date endtime,
			AppointmentStatus status);

	List<Appointment> getAppointmentsWithUseridAndTimeInterval(String userid, Date starttime, Date endtime);

	List<Appointment> getAppointmentsInTimeIntervalWithStatus(Date starttime, Date endtime, AppointmentStatus status);
	
	List<Appointment> getAppointmentsInTimeInterval(Date starttime, Date endtime);
	
	//PUT
	boolean setCustomerIsWaiting(String id, boolean customerIsWaiting);
}

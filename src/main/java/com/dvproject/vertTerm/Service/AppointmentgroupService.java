package com.dvproject.vertTerm.Service;

import java.util.List;

import com.dvproject.vertTerm.Model.*;
import com.dvproject.vertTerm.util.Booker;

/**
 * @author Joshua Müller
 */
public interface AppointmentgroupService {
	// GET
	List<Appointmentgroup> getAll();

	Appointmentgroup getById(String id);

	Appointmentgroup getAppointmentgroupContainingAppointmentID(String id);

	List<Appointmentgroup> getAppointmentgroupsWithStatus(Status status);

	void setPullableAppointment(Appointment appointment);

	void setPullableAppointments(Appointment appointment);

	void testWarningsForAppointments(List<Appointment> appointmentsToTest);
	
	void loadAppointmentgroup(Appointmentgroup appointmentgroupToLoad);
	
	void loadAppointmentgroupWithOverride(Appointmentgroup appointmentgroupToLoad);

	// PUT / POST
	void saveAppointmentgroup(Appointmentgroup appointmentgroup);
	
	//PUT
	boolean startAppointment(Appointment appointment);

	boolean stopAppointment(Appointment appointment);

	// DELETE
	boolean delete(String id);
	
	boolean deleteAppointment(Appointment appointment, Booker booker);
}

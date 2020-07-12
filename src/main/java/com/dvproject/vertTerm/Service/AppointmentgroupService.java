package com.dvproject.vertTerm.Service;

import java.security.Principal;
import java.util.List;

import com.dvproject.vertTerm.Model.*;

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

	void testWarningsForAppointmentgroup(String id);

	void testWarnings(String appointmentid);

	void testWarningsForAppointments(List<Appointment> appointmentsToTest);

	void canBookAppointments(Principal user, Appointmentgroup appointmentgroup);

	// PUT / POST
	String bookAppointmentgroup(String userid, Appointmentgroup appointmentgroup, boolean override);
	
	//PUT
	boolean startAppointment(String appointmentid);

	boolean stopAppointment(String appointmentid);

	// DELETE
	boolean delete(String id);
	
	boolean deleteAppointment(String appointmentid, boolean override);
}

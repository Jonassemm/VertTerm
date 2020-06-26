package com.dvproject.vertTerm.Service;

import java.security.Principal;
import java.util.List;

import com.dvproject.vertTerm.Model.Appointment;
import com.dvproject.vertTerm.Model.Appointmentgroup;
import com.dvproject.vertTerm.Model.Optimizationstrategy;
import com.dvproject.vertTerm.Model.Status;

public interface AppointmentgroupService extends BasicService<Appointmentgroup> {
	//GET
	Appointmentgroup getAppointmentgroupContainingAppointmentID(String id);

	List<Appointmentgroup> getAppointmentgroupsWithStatus(Status status);

	Appointmentgroup getOptimizedSuggestion(Appointmentgroup appointmentgroup,
			Optimizationstrategy optimizationstrategy);

	void setPullableAppointment(Appointment appointment);
	
	void setPullableAppointment();
	
	void testWarnings(String appointmentid);
	
	void canBookAppointments(Principal user, Appointmentgroup appointmentgroup);
	
	//PUT
	String bookAppointmentgroup (String userid, Appointmentgroup appointmentgroup, boolean override);
	
	boolean startAppointment(String appointmentid);
	
	boolean stopAppointment(String appointmentid);
	
	//delete
	boolean deleteAppointment(String id, boolean override);
}

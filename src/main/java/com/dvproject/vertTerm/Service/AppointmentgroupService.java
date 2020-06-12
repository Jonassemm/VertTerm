package com.dvproject.vertTerm.Service;

import java.util.Date;
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
	
	//PUT
	boolean bookAppointmentgroup (String userid, Appointmentgroup appointmentgroup, boolean override);
	
	boolean startAppointment(String appointmentid);
	
	boolean stopAppointment(String appointmentid);
	
	Appointment shiftAppointment (String appointmentId, Date startdate, Date enddate);
}

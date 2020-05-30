package com.dvproject.vertTerm.Service;

import java.util.List;

import com.dvproject.vertTerm.Model.Appointmentgroup;
import com.dvproject.vertTerm.Model.Optimizationstrategy;
import com.dvproject.vertTerm.Model.Status;

public interface AppointmentgroupService extends BasicService<Appointmentgroup> {
	Appointmentgroup getAppointmentgroupWithAppointmentID(String id);

	List<Appointmentgroup> getAppointmentgroupsWithStatus(Status status);

	Appointmentgroup getOptimizedSuggestion(Appointmentgroup appointmentgroup,
			Optimizationstrategy optimizationstrategy);
}

package com.dvproject.vertTerm.Service;

import com.dvproject.vertTerm.Model.Appointmentgroup;

public interface AppointmentgroupService extends BasicService<Appointmentgroup>{
	Appointmentgroup getAppointmentgroupWithAppointmentID (String id);
}

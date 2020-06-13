package com.dvproject.vertTerm.Model;

import java.util.Date;
import java.util.List;
import com.dvproject.vertTerm.Model.Availability;

import com.dvproject.vertTerm.Service.AppointmentService;

public interface Available {
	List<Appointment> getAppointmentsAfterDate(AppointmentService appointmentService, Date startdate);
	
	List<Availability> getAvailabilities();
	
	void isAvailable(Date startdate, Date enddate);
	
	String getId();
}

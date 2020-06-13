package com.dvproject.vertTerm.Model;

import java.util.Date;
import java.util.List;
import com.dvproject.vertTerm.Model.Availability;

import com.dvproject.vertTerm.Service.AppointmentService;

public interface Available {
	List<Appointment> getAppointmentsOfAvailable(AppointmentService appointmentService, Date endOfSeries);
	
	List<Availability> getAvailabilities();
	
	String getId();
}

package com.dvproject.vertTerm.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.dvproject.vertTerm.Model.Appointment;

public abstract class WarningServiceImpl {
	@Autowired
	private AppointmentgroupService appointmentgroupService;
	
	public abstract List<Appointment> getPlannedAppointmentsWithId(String id);
	
	public void testWarningsFor(String id) {
		appointmentgroupService.testWarningsForAppointments(getPlannedAppointmentsWithId(id));
	}

}

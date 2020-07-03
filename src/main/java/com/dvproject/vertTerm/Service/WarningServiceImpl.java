package com.dvproject.vertTerm.Service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.dvproject.vertTerm.Model.Appointment;

public abstract class WarningServiceImpl {
	@Autowired
	private AppointmentgroupService appointmentgroupService;

	abstract List<Appointment> getPlannedAppointmentsWithId(String id);

	public void testWarningsFor(String id) {
		appointmentgroupService.testWarningsForAppointments(getPlannedAppointmentsWithId(id));
	}

	public void testWarningsFor(List<String> ids) {
		List<Appointment> appointments = new ArrayList<>();
		
		ids.forEach(id -> appointments.addAll(getPlannedAppointmentsWithId(id)));
		
		appointmentgroupService.testWarningsForAppointments(appointments);
	}
	
	<T> List<T> getListOfChanged(List<T> oldEntities, List<T> newEntities) {
		List<T> changedEntities = new ArrayList<>();
		changedEntities.addAll(oldEntities);
		changedEntities.addAll(newEntities);

		changedEntities.removeIf(entity -> oldEntities.contains(entity) && newEntities.contains(entity));

		return changedEntities;
	}

}

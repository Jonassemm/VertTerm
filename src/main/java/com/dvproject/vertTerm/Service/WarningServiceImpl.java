package com.dvproject.vertTerm.Service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.dvproject.vertTerm.Model.Appointment;
import com.dvproject.vertTerm.Model.Appointmentgroup;

public abstract class WarningServiceImpl {
	@Autowired
	private AppointmentgroupService appointmentgroupService;

	abstract List<Appointment> getPlannedAppointmentsWithId(String id);

	public void testWarningsFor(String id) {
		testWarningsForAppointments(getPlannedAppointmentsWithId(id));
	}

	public void testWarningsFor(List<String> ids) {
		List<Appointment> appointments = new ArrayList<>();

		ids.forEach(id -> appointments.addAll(getPlannedAppointmentsWithId(id)));

		testWarningsForAppointments(appointments);
	}

	public void testWarningsForAppointments(List<Appointment> appointments) {
		appointmentgroupService.testWarningsForAppointments(appointments);
	}

	public void testProcedureRelationWarning(Appointment appointment) {
		Appointmentgroup appointmentgroup = appointmentgroupService
				.getAppointmentgroupContainingAppointmentID(appointment.getId());

		appointmentgroup.testProcedureRelations(true);
	}

	<T> boolean haveChanged(List<T> oldEntities, List<T> newEntities) {
		return oldEntities.stream().allMatch(oldEntity -> newEntities.contains(oldEntity));
	}

}

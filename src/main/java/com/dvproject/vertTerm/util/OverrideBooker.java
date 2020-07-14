package com.dvproject.vertTerm.util;

import java.util.ArrayList;
import java.util.List;

import com.dvproject.vertTerm.Model.Appointment;
import com.dvproject.vertTerm.Model.Appointmentgroup;
import com.dvproject.vertTerm.Model.User;
import com.dvproject.vertTerm.Service.AppointmentService;
import com.dvproject.vertTerm.Service.RestrictionService;

public class OverrideBooker extends Booker {
	public OverrideBooker () {
		super();
	}

	public OverrideBooker (Appointmentgroup appointmentgroupToBook) {
		super(appointmentgroupToBook);
	}

	@Override
	protected void testAppointmentgroup(AppointmentService appointmentService, RestrictionService restrictionService,
			User booker) {
		AppointmentTester tester = new OverrideAppointmentTester(new ArrayList<>());
		List<Appointment> appointments = appointmentgroupToBook.getAppointments();

		appointmentgroupToBook.canBookProcedures(booker);
		testProcedureRelations();
		appointments.forEach(app -> tester.testAppointment(app, appointmentService, restrictionService));
	}

	@Override
	public void testProcedureRelations() {
		appointmentgroupToBook.testProcedureRelations(true);
	}

}

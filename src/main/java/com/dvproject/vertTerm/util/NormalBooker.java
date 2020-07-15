package com.dvproject.vertTerm.util;

import java.util.ArrayList;
import java.util.List;

import com.dvproject.vertTerm.Model.*;
import com.dvproject.vertTerm.Service.AppointmentService;
import com.dvproject.vertTerm.Service.RestrictionService;

/**
 * @author Joshua Müller
 */
public class NormalBooker extends Booker {
	public NormalBooker () {
		super();
	}

	public NormalBooker (Appointmentgroup appointmentgroupToBook) {
		super(appointmentgroupToBook);
	}

	@Override
	protected void testAppointmentgroup(AppointmentService appointmentService, RestrictionService restrictionService,
			User booker) {
		AppointmentTester tester = new NormalAppointmentTester(new ArrayList<>());
		List<Appointment> appointments = appointmentgroupToBook.getAppointments();

		appointmentgroupToBook.canBookProcedures(booker);
		testProcedureRelations();
		appointments.forEach(app -> tester.testAppointment(app, appointmentService, restrictionService));
	}

	@Override
	public void testProcedureRelations() {
		appointmentgroupToBook.testProcedureRelations(false);
	}

	@Override
	protected void testAppointmentgroup(AppointmentService appointmentService, RestrictionService restrictionService) {
		testAppointmentgroup(appointmentService, restrictionService, new User());
	}

}

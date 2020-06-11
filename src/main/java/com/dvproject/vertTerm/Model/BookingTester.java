package com.dvproject.vertTerm.Model;

import java.util.List;

import com.dvproject.vertTerm.Service.AppointmentServiceImpl;
import com.dvproject.vertTerm.Service.RestrictionService;

public abstract class BookingTester {
	Appointment appointment;

	public void testAll(AppointmentServiceImpl appointmentService, RestrictionService restrictionService,
			List<TimeInterval> timeIntervallsOfAppointments) {
		this.testAppointmentTimes(timeIntervallsOfAppointments);
		this.testEmployees();
		this.testProcedurePositions();
		this.testResources();
		this.testProcedureResourceTypes();
		this.testProcedure();
		this.testAvailabilities();
		this.testRestrictions(restrictionService);
		this.testAppointment(appointmentService);
	}
	
	public void setAppointment(Appointment appointment) {
		this.appointment = appointment;
	}

	abstract void testAppointmentTimes(List<TimeInterval> timeIntervallsOfAppointments);

	abstract void testEmployees();

	abstract void testProcedurePositions();

	abstract void testResources();

	abstract void testProcedureResourceTypes();

	abstract void testProcedure();

	abstract void testAvailabilities();

	abstract void testRestrictions(RestrictionService restrictionService);

	abstract void testAppointment(AppointmentServiceImpl appointmentService);
}

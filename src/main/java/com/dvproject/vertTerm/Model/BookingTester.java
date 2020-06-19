package com.dvproject.vertTerm.Model;

import java.util.List;

import com.dvproject.vertTerm.Service.AppointmentServiceImpl;
import com.dvproject.vertTerm.Service.RestrictionService;

public abstract class BookingTester {
	Appointment appointment;
	
	public BookingTester () {}
	
	public BookingTester (Appointment appointment) {
		this.appointment = appointment;
	}

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

	public abstract void testAppointmentTimes(List<TimeInterval> timeIntervallsOfAppointments);

	public abstract void testEmployees();

	public abstract void testProcedurePositions();

	public abstract void testResources();

	public abstract void testProcedureResourceTypes();

	public abstract void testProcedure();

	public abstract void testAvailabilities();

	public abstract void testRestrictions(RestrictionService restrictionService);

	public abstract void testAppointment(AppointmentServiceImpl appointmentService);
}

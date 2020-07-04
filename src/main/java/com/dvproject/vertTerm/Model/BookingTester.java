package com.dvproject.vertTerm.Model;

import java.util.List;

import com.dvproject.vertTerm.Service.AppointmentServiceImpl;
import com.dvproject.vertTerm.Service.RestrictionService;

public abstract class BookingTester {
	protected Appointment appointment;
	private List<TimeInterval> timeIntervallsOfAppointments;

	public BookingTester () {}

	public BookingTester (Appointment appointment) {
		this();
		setAppointment(appointment);
	}

	public BookingTester (Appointment appointment, List<TimeInterval> timeIntervallsOfAppointments) {
		this(appointment);
		this.timeIntervallsOfAppointments = timeIntervallsOfAppointments;
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

	public void testAll(Appointment appointment, AppointmentServiceImpl appointmentService,
			RestrictionService restrictionService, List<TimeInterval> timeIntervallsOfAppointments) {
		setAppointment(appointment);
		testAll(appointmentService, restrictionService, timeIntervallsOfAppointments);
	}

	public Appointment testAll(Appointment appointment, AppointmentServiceImpl appointmentService,
			RestrictionService restrictionService) {
		testAll(appointment, appointmentService, restrictionService, timeIntervallsOfAppointments);
		return appointment;
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

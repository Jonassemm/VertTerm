package com.dvproject.vertTerm.util;

import java.util.List;

import com.dvproject.vertTerm.Model.Appointment;
import com.dvproject.vertTerm.Model.TimeInterval;
import com.dvproject.vertTerm.Service.AppointmentService;
import com.dvproject.vertTerm.Service.RestrictionService;

/**
 * @author Joshua Müller
 */
public abstract class AppointmentTester {
	protected Appointment appointment;
	private List<TimeInterval> timeIntervallsOfAppointments;

	public AppointmentTester () {}

	public AppointmentTester (Appointment appointment) {
		this();
		setAppointment(appointment);
	}

	public AppointmentTester (Appointment appointment, List<TimeInterval> timeIntervallsOfAppointments) {
		this(appointment);
		this.timeIntervallsOfAppointments = timeIntervallsOfAppointments;
	}
	
	public AppointmentTester (List<TimeInterval> timeIntervallsOfAppointments) {
		this.timeIntervallsOfAppointments = timeIntervallsOfAppointments;
	}

	public void testAppointment(AppointmentService appointmentService, RestrictionService restrictionService,
			List<TimeInterval> timeIntervallsOfAppointments) {
		testAppointmentTimes(timeIntervallsOfAppointments);
		testEmployees();
		testProcedurePositions();
		testResources();
		testProcedureResourceTypes();
		testProcedure();
		testAvailabilities();
		testRestrictions(restrictionService);
		testAppointment(appointmentService);
	}

	public void testAppointment(Appointment appointment, AppointmentService appointmentService,
			RestrictionService restrictionService, List<TimeInterval> timeIntervallsOfAppointments) {
		setAppointment(appointment);
		testAppointment(appointmentService, restrictionService, timeIntervallsOfAppointments);
	}

	public Appointment testAppointment(Appointment appointment, AppointmentService appointmentService,
			RestrictionService restrictionService) {
		testAppointment(appointment, appointmentService, restrictionService, timeIntervallsOfAppointments);
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

	public abstract void testAppointment(AppointmentService appointmentService);

}

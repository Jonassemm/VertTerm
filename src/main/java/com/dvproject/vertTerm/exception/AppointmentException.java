package com.dvproject.vertTerm.exception;

import com.dvproject.vertTerm.Model.Appointment;

/**
 * @author Joshua Müller
 */
public class AppointmentException extends RuntimeException {
	private static final long serialVersionUID = -1545205892732788893L;
	
	private Appointment appointment;
	
	public AppointmentException (String message, Appointment appointment) {
		super(message);
		this.appointment = appointment;
	}

	public Appointment getAppointment() {
		return appointment;
	}

}

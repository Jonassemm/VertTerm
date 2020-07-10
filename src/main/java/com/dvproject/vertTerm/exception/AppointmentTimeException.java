package com.dvproject.vertTerm.exception;

import com.dvproject.vertTerm.Model.Appointment;

/**
 * @author Joshua Müller
 */
public class AppointmentTimeException extends RuntimeException {
	private static final long serialVersionUID = -4718176959272485899L;
	
	private Appointment appointment;
	
	public AppointmentTimeException (String message, Appointment appointment) {
		super(message);
		this.appointment = appointment;
	}

	public Appointment getAppointment() {
		return appointment;
	}
	
}

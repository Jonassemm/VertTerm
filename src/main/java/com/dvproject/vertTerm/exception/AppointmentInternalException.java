package com.dvproject.vertTerm.exception;

import java.util.List;

import com.dvproject.vertTerm.Model.Appointment;

public class AppointmentInternalException extends RuntimeException {
	private static final long serialVersionUID = 244933595529555739L;

	private List<Appointment> appointments;
	private String message;
	private Appointment appointmentWithProblem;

	public AppointmentInternalException (List<Appointment> appointments, String message,
			Appointment appointmentWithProblem) {
		super();
		this.appointments           = appointments;
		this.message                = message;
		this.appointmentWithProblem = appointmentWithProblem;
	}

	public Appointment getAppointmentOfException() {
		return appointmentWithProblem;
	}

	public List<Appointment> getAppointments() {
		return appointments;
	}

	public String getMessage() {
		return message;
	}

}

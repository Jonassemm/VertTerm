package com.dvproject.vertTerm.Model;

import java.io.Serializable;
import java.time.Duration;
import java.util.Date;
import java.util.List;

import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import com.dvproject.vertTerm.Service.AppointmentService;
import com.dvproject.vertTerm.exception.AvailabilityException;

@Document("user")
public class Employee extends User implements Serializable, Available {
	private static final long serialVersionUID = -4432631544443788288L;

	@DBRef
	private List<Position> positions;

	public List<Position> getPositions() {
		return positions;
	}

	public void setPositions(List<Position> positions) {
		this.positions = positions;
	}
	
	public void isAvailable (Date startdate, Date enddate) {
		for (Availability availability : super.getAvailabilities()) {
			if (availability.isAvailableBetween(startdate, enddate)) {
				return;
			}
		}
		
		throw new AvailabilityException("No availability for the employee " + super.getFirstName() + " " + super.getLastName());
	}

	@Override
	public List<Appointment> getAppointmentsOfAvailable(AppointmentService appointmentService, Date endOfSeries) {
		return appointmentService.getAppointmentsOfEmployee(super.getId(), endOfSeries);
	}

}
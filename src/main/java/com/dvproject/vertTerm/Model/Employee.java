package com.dvproject.vertTerm.Model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;

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

	public void isAvailable(Date startdate, Date enddate) {
		if (!getAvailabilities().stream()
				.anyMatch(availability -> availability.isAvailableBetween(startdate, enddate))) {
			throw new AvailabilityException("No availability for the employee " + getFirstName() + " " + getLastName());
		}
	}


	@Override
	public List<Appointment> getAppointmentsAfterDate(AppointmentService appointmentService, Date startdate) {
		List<Appointment> appointments = appointmentService.getAppointmentsOf(this, startdate);

		for (Appointment appointment : appointments) {
			List<Employee> employees = appointment.getBookedEmployees();
			Employee employee;
			for (int i = 0; i < employees.size(); i++) {
				employee = employees.get(i);

				if (employee.getId().equals(getId())) {
					employees.set(i, this);
					break;
				}
			}
		}

		return appointments;
	}

}

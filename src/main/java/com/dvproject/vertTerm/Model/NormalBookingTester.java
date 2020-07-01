package com.dvproject.vertTerm.Model;

import java.util.List;

import com.dvproject.vertTerm.Service.AppointmentServiceImpl;
import com.dvproject.vertTerm.Service.RestrictionService;
import com.dvproject.vertTerm.exception.AppointmentException;
import com.dvproject.vertTerm.exception.AppointmentInternalException;

public class NormalBookingTester extends BookingTester {

	public NormalBookingTester () {
		super();
	}

	public NormalBookingTester (List<TimeInterval> timeIntervallsOfAppointments) {
		super(null, timeIntervallsOfAppointments);
	}

	public NormalBookingTester (Appointment appointment) {
		super(appointment);
	}

	@Override
	public void testAppointmentTimes(List<TimeInterval> timeIntervallsOfAppointments) {
		appointment.testPlannedTimes();
		appointment.testOverlapping(timeIntervallsOfAppointments);
	}

	@Override
	public void testEmployees() {
		appointment.testEmployeesOfAppointment();
	}

	@Override
	public void testProcedurePositions() {
		appointment.testBookedEmployeesAgainstPositionsOfProcedure();
	}

	@Override
	public void testResources() {
		appointment.testResourcesOfAppointment();
	}

	@Override
	public void testProcedureResourceTypes() {
		appointment.testBookedResourcesAgainstResourceTypesOfProcedure();
	}

	@Override
	public void testProcedure() {
		appointment.testProcedureDuration();

	}

	@Override
	public void testAvailabilities() {
		appointment.testAvailabilitiesOfProcedure_Employees_Resources();
	}

	@Override
	public void testRestrictions(RestrictionService restrictionService) {
		appointment.testRestrictions(restrictionService);
	}

	@Override
	public void testAppointment(AppointmentServiceImpl appointmentService) {
		appointment.testDistinctBookedAttributes();
		try {
			appointment.testBlockage(appointmentService);
		} catch (AppointmentInternalException ex) {
			throw new AppointmentException(ex.getMessage(), ex.getAppointmentOfException());
		}
	}

}

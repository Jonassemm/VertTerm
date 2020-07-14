package com.dvproject.vertTerm.util;

import java.util.List;

import com.dvproject.vertTerm.Model.Appointment;
import com.dvproject.vertTerm.Model.Appointmentgroup;
import com.dvproject.vertTerm.Model.TimeInterval;
import com.dvproject.vertTerm.Service.AppointmentService;
import com.dvproject.vertTerm.Service.AppointmentServiceImpl;
import com.dvproject.vertTerm.Service.RestrictionService;
import com.dvproject.vertTerm.exception.AppointmentException;
import com.dvproject.vertTerm.exception.AppointmentInternalException;

public class NormalAppointmentTester extends AppointmentTester {

	public NormalAppointmentTester () {
		super();
	}

	public NormalAppointmentTester (List<TimeInterval> timeIntervallsOfAppointments) {
		super(null, timeIntervallsOfAppointments);
	}

	public NormalAppointmentTester (Appointment appointment) {
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
	public void testAppointment(AppointmentService appointmentService) {
		appointment.testDistinctBookedAttributes();
		try {
			appointment.testBlockage(appointmentService);
		} catch (AppointmentInternalException ex) {
			throw new AppointmentException(ex.getMessage(), ex.getAppointmentOfException());
		}
	}

}

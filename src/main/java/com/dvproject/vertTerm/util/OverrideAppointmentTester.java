package com.dvproject.vertTerm.util;

import java.util.List;
import java.util.stream.Collectors;

import com.dvproject.vertTerm.Model.Appointment;
import com.dvproject.vertTerm.Model.TimeInterval;
import com.dvproject.vertTerm.Model.Warning;
import com.dvproject.vertTerm.Service.*;
import com.dvproject.vertTerm.exception.*;

public class OverrideAppointmentTester extends AppointmentTester {

	public OverrideAppointmentTester () {
		super();
	}

	public OverrideAppointmentTester (List<TimeInterval> timeIntervallsOfAppointments) {
		super(null, timeIntervallsOfAppointments);
	}

	@Override
	public void testAppointmentTimes(List<TimeInterval> timeIntervallsOfAppointments) {
		try {
			appointment.testPlannedTimes();
			appointment.testOverlapping(timeIntervallsOfAppointments);
		} catch (AppointmentTimeException ex) {
			appointment.addWarnings(Warning.APPOINTMENT_TIME_WARNING);
		}
	}

	@Override
	public void testEmployees() {
		try {
			appointment.testEmployeesOfAppointment();
		} catch (EmployeeException ex) {
			appointment.addWarnings(Warning.EMPLOYEE_WARNING);
		}

	}

	@Override
	public void testProcedurePositions() {
		try {
			appointment.testBookedEmployeesAgainstPositionsOfProcedure();
		} catch (PositionException ex) {
			appointment.addWarnings(Warning.POSITION_WARINING);
		}
	}

	@Override
	public void testResources() {
		try {
			appointment.testResourcesOfAppointment();
		} catch (ResourceException ex) {
			appointment.addWarnings(Warning.RESOURCE_WARNING);
		}
	}

	@Override
	public void testProcedureResourceTypes() {
		try {
			appointment.testBookedResourcesAgainstResourceTypesOfProcedure();
		} catch (ResourceTypeException ex) {
			appointment.addWarnings(Warning.RESOURCETYPE_WARNING);
		}
	}

	@Override
	public void testProcedure() {
		try {
			appointment.testProcedureDuration();
		} catch (ProcedureException ex) {
			appointment.addWarnings(Warning.PROCEDURE_WARNING);
		}
	}

	@Override
	public void testAvailabilities() {
		try {
			appointment.testAvailabilitiesOfProcedure_Employees_Resources();
		} catch (AvailabilityException ex) {
			appointment.addWarnings(Warning.AVAILABILITY_WARNING);
		}
	}

	@Override
	public void testRestrictions(RestrictionService restrictionService) {
		try {
			appointment.testRestrictions(restrictionService);
		} catch (RestrictionException ex) {
			appointment.addWarnings(Warning.RESTRICTION_WARNING);
		}
	}

	@Override
	public void testAppointment(AppointmentService appointmentService) {
		try {
			appointment.testDistinctBookedAttributes();
			appointment.testBlockage(appointmentService);
		} catch (AppointmentException ex) {
			appointment.addWarnings(Warning.APPOINTMENT_WARNING);
		} catch (AppointmentInternalException ex) {
			appointment.addWarnings(Warning.APPOINTMENT_WARNING);

			List<Appointment> failedAppointments = ex.getAppointments()
								.stream()
								.filter(app -> app.getBookedProcedure() != null)
								.collect(Collectors.toList());

			failedAppointments.forEach(app -> {
				app.addWarnings(Warning.APPOINTMENT_WARNING);
				appointmentService.update(app);
			});
		}
	}

}

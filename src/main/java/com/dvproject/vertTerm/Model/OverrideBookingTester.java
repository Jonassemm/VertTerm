package com.dvproject.vertTerm.Model;

import java.util.List;

import com.dvproject.vertTerm.Service.AppointmentServiceImpl;
import com.dvproject.vertTerm.Service.RestrictionService;
import com.dvproject.vertTerm.exception.AppointmentException;
import com.dvproject.vertTerm.exception.AppointmentTimeException;
import com.dvproject.vertTerm.exception.AppointmentInternalException;
import com.dvproject.vertTerm.exception.AvailabilityException;
import com.dvproject.vertTerm.exception.EmployeeException;
import com.dvproject.vertTerm.exception.PositionException;
import com.dvproject.vertTerm.exception.ProcedureException;
import com.dvproject.vertTerm.exception.ResourceException;
import com.dvproject.vertTerm.exception.ResourceTypeException;
import com.dvproject.vertTerm.exception.RestrictionException;

public class OverrideBookingTester extends BookingTester {

	public OverrideBookingTester () {
		super();
	}

	public OverrideBookingTester (List<TimeInterval> timeIntervallsOfAppointments) {
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
	public void testAppointment(AppointmentServiceImpl appointmentService) {
		try {
			appointment.testDistinctBookedAttributes();
			appointment.testBlockage(appointmentService);
		} catch (AppointmentException ex) {
			appointment.addWarnings(Warning.APPOINTMENT_WARNING);
		} catch (AppointmentInternalException ex) {
			appointment.addWarnings(Warning.APPOINTMENT_WARNING);
			
			ex.getAppointments().forEach(app -> { 
				app.addWarnings(Warning.APPOINTMENT_WARNING); 
				appointmentService.update(app);
			});
		}
	}
}

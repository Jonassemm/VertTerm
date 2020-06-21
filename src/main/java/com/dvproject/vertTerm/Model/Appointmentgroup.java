package com.dvproject.vertTerm.Model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;

import com.dvproject.vertTerm.Service.AppointmentServiceImpl;
import com.dvproject.vertTerm.Service.RestrictionService;
import com.dvproject.vertTerm.exception.ProcedureException;
import com.dvproject.vertTerm.exception.ProcedureRelationException;

public class Appointmentgroup {
	@Id
	private String id;

	@NotNull
	private Status status;

	@DBRef
	@NotEmpty
	private List<Appointment> appointments;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public boolean hasNoAppointmentIdSet() {
		return appointments.stream().allMatch(appointment -> !appointment.hasId());
	}

	public boolean hasAllAppointmentIdSet() {
		return appointments.stream().allMatch(Appointment::hasId);
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public List<Appointment> getAppointments() {
		return appointments;
	}

	public void setAppointments(List<Appointment> appointments) {
		this.appointments = appointments;
	}

	public void testBookability(RestrictionService restrictionService, AppointmentServiceImpl appointmentService,
			BookingTester bookingTester) {
		List<TimeInterval> timeIntervallsOfAppointments = new ArrayList<>();

		appointments.forEach(appointment -> bookingTester.testAll(appointment, appointmentService, restrictionService,
				timeIntervallsOfAppointments));
	}

	public void testProcedureRelations() {
		// procedure.id -> appointment
		Map<String, Appointment> appointmentsMap = new HashMap<>();
		// procedure.id -> procedure
		Map<String, Procedure> proceduresMap = new HashMap<>();

		// populate maps
		for (Appointment appointment : appointments) {
			Procedure procedureOfAppointment = appointment.getBookedProcedure();
			String id = procedureOfAppointment.getId();

			if (proceduresMap.containsKey(id)) {
				throw new ProcedureException("Two different appointments of one procedure", procedureOfAppointment);
			}

			proceduresMap.put(id, appointment.getBookedProcedure());
			appointmentsMap.put(id, appointment);
		}

		for (Appointment appointment : appointments) {
			Procedure procedure = proceduresMap.get(appointment.getBookedProcedure().getId());
			List<ProcedureRelation> precedingprocedures = procedure.getPrecedingRelations();
			List<ProcedureRelation> subsequentprocedures = procedure.getSubsequentRelations();

			// test all precedingRelations
			if (precedingprocedures != null) {
				for (ProcedureRelation precedingProcedureRelation : precedingprocedures) {
					if (proceduresMap.containsKey(precedingProcedureRelation.getProcedure().getId())) {
						Appointment appointmentToTest = appointmentsMap
								.get(precedingProcedureRelation.getProcedure().getId());

						// test the procedurerelation with time data from appointment
						if (!precedingProcedureRelation.testConformatyOfDates(
								getCalendar(appointmentToTest.getPlannedEndtime()),
								getCalendar(appointment.getPlannedStarttime()))) {
							throw new ProcedureRelationException("Time-condition of preceding procedurerelation failed",
									precedingProcedureRelation);
						}
					} else {
						throw new ProcedureRelationException("Missing appointment for procedure to complete booking",
								precedingProcedureRelation);
					}
				}
			}

			// test all subsequentRelations
			if (subsequentprocedures != null) {
				for (ProcedureRelation subsequentProcedureRelation : subsequentprocedures) {
					if (proceduresMap.containsKey(subsequentProcedureRelation.getProcedure().getId())) {
						Appointment appointmentToTest = appointmentsMap
								.get(subsequentProcedureRelation.getProcedure().getId());

						// test the procedurerelation with time data from appointment
						if (!subsequentProcedureRelation.testConformatyOfDates(
								getCalendar(appointment.getPlannedEndtime()),
								getCalendar(appointmentToTest.getPlannedStarttime()))) {
							throw new ProcedureRelationException(
									"Time-condition of subsequent procedurerelation failed",
									subsequentProcedureRelation);
						}
					} else {
						throw new ProcedureRelationException("Missing appointment for procedure to complete booking",
								subsequentProcedureRelation);
					}
				}
			}
		}
	}

	private Calendar getCalendar(Date date) {
		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		calendar.setTime(date);
		return calendar;
	}

}

package com.dvproject.vertTerm.Model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.stream.Collectors;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;

import com.dvproject.vertTerm.Service.AppointmentServiceImpl;
import com.dvproject.vertTerm.Service.RestrictionService;
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
		return !appointments.stream().anyMatch(Appointment::hasId);
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

	public void resetAllWarnings() {
		appointments.forEach(app -> app.setWarnings(new ArrayList<Warning>()));
	}

	public void testBookability(RestrictionService restrictionService, AppointmentServiceImpl appointmentService,
			BookingTester bookingTester) {
		appointments.forEach(appointment -> bookingTester.testAll(appointment, appointmentService, restrictionService));
	}

	public boolean hasDistinctProcedures() {
		return appointments.stream().map(app -> app.getBookedProcedure().getId()).distinct().count() == appointments
				.size();
	}

	public void testProcedureRelations(boolean override) {
		// procedure.id -> appointment
		Map<String, Appointment> appointmentsMap = new HashMap<>();
		// procedure.id -> procedure
		Map<String, Procedure> proceduresMap = new HashMap<>();

		List<Appointment> appointments = this.appointments.stream()
				.filter(app -> app.getStatus() != AppointmentStatus.DELETED).collect(Collectors.toList());

		List<Appointment> appointmentsWithProblem = new ArrayList<>();
		ProcedureRelationException pre = null;

		// populate maps
		for (Appointment appointment : appointments) {
			Procedure procedureOfAppointment = appointment.getBookedProcedure();
			String id = procedureOfAppointment.getId();

			proceduresMap.put(id, procedureOfAppointment);
			appointmentsMap.put(id, appointment);
		}

		for (Appointment appointment : appointments) {
			try {
				testProcedureRelationOfAppointment(appointment, appointmentsMap, proceduresMap);
			} catch (ProcedureRelationException ex) {
				appointmentsWithProblem.add(appointment);
				if (pre == null)
					pre = ex;
			}
		}

		if (override) {
			appointmentsWithProblem.forEach(appointment -> appointment.addWarning(Warning.PROCEDURE_RELATION_WARNING));
		} else {
			if (pre != null)
				throw pre;
		}
	}

	public void testProcedureRelationOfAppointment(Appointment appointment, Map<String, Appointment> appointmentsMap,
			Map<String, Procedure> proceduresMap) {
		Procedure procedure = proceduresMap.get(appointment.getBookedProcedure().getId());
		List<ProcedureRelation> precedingprocedures = procedure.getPrecedingRelations();
		List<ProcedureRelation> subsequentprocedures = procedure.getSubsequentRelations();

		// test all precedingRelations
		if (precedingprocedures != null) {
			for (ProcedureRelation precedingProcedureRelation : precedingprocedures) {
				String procedureId = precedingProcedureRelation.getProcedure().getId();
				if (proceduresMap.containsKey(procedureId)) {
					Appointment appointmentToTest = appointmentsMap.get(procedureId);

					// test the procedurerelation with time data from appointment
					if (!precedingProcedureRelation.testConformatyOfDates(getCalendar(appointmentToTest.getPlannedEndtime()),
							getCalendar(appointment.getPlannedStarttime())))
						throw new ProcedureRelationException("Time-condition of preceding procedurerelation failed",
								precedingProcedureRelation);
				} else
					throw new ProcedureRelationException("Missing appointment for procedure to complete booking",
							precedingProcedureRelation);
			}
		}

		// test all subsequentRelations
		if (subsequentprocedures != null) {
			for (ProcedureRelation subsequentProcedureRelation : subsequentprocedures) {
				String procedureId = subsequentProcedureRelation.getProcedure().getId();
				if (proceduresMap.containsKey(procedureId)) {
					Appointment appointmentToTest = appointmentsMap.get(procedureId);

					// test the procedurerelation with time data from appointment
					if (!subsequentProcedureRelation.testConformatyOfDates(getCalendar(appointment.getPlannedEndtime()),
							getCalendar(appointmentToTest.getPlannedStarttime())))
						throw new ProcedureRelationException("Time-condition of subsequent procedurerelation failed",
								subsequentProcedureRelation);
				} else
					throw new ProcedureRelationException("Missing appointment for procedure to complete booking",
							subsequentProcedureRelation);
			}
		}
	}

	private Calendar getCalendar(Date date) {
		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		calendar.setTime(date);
		return calendar;
	}

}

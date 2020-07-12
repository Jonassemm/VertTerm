package com.dvproject.vertTerm.Model;

import java.time.Duration;
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

import com.dvproject.vertTerm.Controller.RessourceController;
import com.dvproject.vertTerm.Service.*;
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
		return appointments.stream().noneMatch(Appointment::hasId);
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

	private Appointment findLatestAppointment(){
		Appointment result = this.appointments.get(0);
		for(Appointment appointment : this.getAppointments()){
			if(appointment.getPlannedEndtime().after(result.getPlannedEndtime())){
				result = appointment;
			}
		}
		return result;
	}

	private Appointment findEarliestAppointment(){
		Appointment result = this.appointments.get(0);
		for(Appointment appointment : this.getAppointments()){
			if(appointment.getPlannedEndtime().before(result.getPlannedEndtime())){
				result = appointment;
			}
		}
		return result;
	}

	private boolean allNeededBookablesFound() {
		for (Appointment appointment : this.getAppointments()){
			if(!appointment.allNeededBookablesFound()){
				return false;
			}
		}
		return true;
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
					Date plannedEndtime = appointmentToTest.getPlannedEndtime();
					Date plannedStarttime = appointment.getPlannedStarttime();

					if (!hasCorrectPlannedTimeValue(precedingProcedureRelation, plannedEndtime, plannedStarttime))
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
					Date plannedEndtime = appointment.getPlannedEndtime();
					Date plannedStarttime = appointmentToTest.getPlannedStarttime();

					if (!hasCorrectPlannedTimeValue(subsequentProcedureRelation, plannedEndtime, plannedStarttime))
						throw new ProcedureRelationException("Time-condition of subsequent procedurerelation failed",
								subsequentProcedureRelation);
				} else
					throw new ProcedureRelationException("Missing appointment for procedure to complete booking",
							subsequentProcedureRelation);
			}
		}
	}

	private boolean hasCorrectPlannedTimeValue(ProcedureRelation procedureRelation, Date endtime, Date starttime) {
		return endtime.before(starttime)
				&& procedureRelation.testConformatyOfDates(getCalendar(endtime), getCalendar(starttime));
	}

	private Calendar getCalendar(Date date) {
		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		calendar.setTime(date);
		return calendar;
	}

	private Appointment getOrCreateAppointmentWithProcedure(Procedure procedure, User customer){
		for(Appointment appointment : this.getAppointments()){
			if(appointment.getBookedProcedure().getId().equals(procedure.getId())){
				return appointment;
			}
		}
		Appointment appointment = new Appointment();
		appointment.setBookedCustomer(customer);
		appointment.setBookedProcedure(procedure);
		appointment.setStatus(AppointmentStatus.OPEN);
		this.getAppointments().add(appointment);
		return appointment;
	}

	private int DaysNeededForAppointments(Appointment appointment1, Appointment appointment2){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(appointment1.getPlannedStarttime());
		int date1 = calendar.get(Calendar.DATE);
		calendar.setTime(appointment2.getPlannedEndtime());
		int date2 = calendar.get(Calendar.DATE);
		return date2 - date1;
	}

	public void optimizeForLeastDays(AppointmentService appointmentService, ResourceService resourceService, EmployeeService employeeService, Appointment optimizationStart) {
		this.optimizeForLeastWaitingTime(appointmentService, resourceService, employeeService, optimizationStart);

		if(!this.allNeededBookablesFound()){
			return;
		}

		Date optimum = null;
		int days = DaysNeededForAppointments(this.findEarliestAppointment(), this.findLatestAppointment());
		for (int i = 0; i < 10; i++) {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(optimizationStart.getPlannedStarttime());
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			optimizationStart.setPlannedStarttime(calendar.getTime());

			this.optimizeForLeastWaitingTime(appointmentService, resourceService, employeeService, optimizationStart);

			int newDays = DaysNeededForAppointments(this.findEarliestAppointment(), this.findLatestAppointment());
			if(newDays < days && this.allNeededBookablesFound()){
				optimum = optimizationStart.getPlannedStarttime();
			}
		}

		optimizationStart.setPlannedStarttime(optimum);
		this.optimizeForLeastWaitingTime(appointmentService, resourceService, employeeService, optimizationStart);
	}

	public void optimizeForLeastWaitingTime(AppointmentService appointmentService, ResourceService resourceService, EmployeeService employeeService, Appointment optimizationStart){
		this.optimizeAppointmentsForEarliestEnd(appointmentService, resourceService, employeeService, optimizationStart);
		for (Appointment appointment : this.getAppointments()) {
			appointment.setStatus(AppointmentStatus.OPEN);
		}
		this.optimizeAppointmentsForLatestBeginning(appointmentService, resourceService, employeeService, this.findLatestAppointment());
	}

	public void optimizeAppointmentsForEarliestEnd(AppointmentService appointmentService, ResourceService resourceService, EmployeeService employeeService, Appointment optimizationStart) {
		optimizationStart.optimizeAndPopulateForEarliestEnd(appointmentService, resourceService, employeeService);
		for (ProcedureRelation relation : optimizationStart.getBookedProcedure().getSubsequentRelations()) {
			Appointment subsequentAppointment = this.getOrCreateAppointmentWithProcedure(relation.getProcedure(), optimizationStart.getBookedCustomer());
			Date MinDate;
			if (relation.getMinDifference() != null) {
				MinDate = new Date(relation.getMinDifference().toMillis()
						+ optimizationStart.getPlannedEndtime().getTime());
			} else {
				MinDate = optimizationStart.getPlannedEndtime();
			}
			if (subsequentAppointment.getStatus() != AppointmentStatus.RECOMMENDED
					|| MinDate.before(subsequentAppointment.getPlannedStarttime())) {
				subsequentAppointment.setPlannedStarttime(MinDate);
				this.optimizeAppointmentsForEarliestEnd(appointmentService, resourceService, employeeService, subsequentAppointment);
			}
		}

		for (ProcedureRelation relation : optimizationStart.getBookedProcedure().getPrecedingRelations()) {
			Appointment precedingAppointment = this.getOrCreateAppointmentWithProcedure(relation.getProcedure(), optimizationStart.getBookedCustomer());
			Date MinDate;
			if (relation.getMinDifference() == null) {
				MinDate = new Date(optimizationStart.getPlannedStarttime().getTime()
						- precedingAppointment.getBookedProcedure().getDuration().toMillis());
			} else {
				MinDate = new Date(optimizationStart.getPlannedStarttime().getTime()
						- relation.getMinDifference().toMillis()
						- precedingAppointment.getBookedProcedure().getDuration().toMillis());
			}
			if(precedingAppointment.getStatus() != AppointmentStatus.RECOMMENDED){
				precedingAppointment.setPlannedStarttime(MinDate);
				this.optimizeAppointmentsForLatestBeginning(appointmentService, resourceService, employeeService, precedingAppointment);
			}
			if (relation.getMaxDifference() != null) {
				Date MaxDate = new Date(optimizationStart.getPlannedStarttime().getTime()
						- precedingAppointment.getBookedProcedure().getDuration().toMillis()
						- relation.getMaxDifference().toMillis());
				if (precedingAppointment.getPlannedStarttime() == null
						|| precedingAppointment.getPlannedStarttime().before(MaxDate)) {
					precedingAppointment.setPlannedStarttime(MaxDate);
					this.optimizeAppointmentsForEarliestEnd(appointmentService, resourceService, employeeService, precedingAppointment);
				}
			}
		}
	}

	public void optimizeAppointmentsForLatestBeginning(AppointmentService appointmentService, ResourceService resourceService, EmployeeService employeeService, Appointment optimizationStart){
		optimizationStart.optimizeAndPopulateForLatestBeginning(appointmentService, resourceService, employeeService);
		optimizationStart.setStatus(AppointmentStatus.RECOMMENDED);
		for(ProcedureRelation relation : optimizationStart.getBookedProcedure().getPrecedingRelations()){
			Appointment precedingAppointment = this.getOrCreateAppointmentWithProcedure(relation.getProcedure(), optimizationStart.getBookedCustomer());
			Date MinDate;
			if (relation.getMinDifference() == null) {
				MinDate = new Date(optimizationStart.getPlannedStarttime().getTime()
						- precedingAppointment.getBookedProcedure().getDuration().toMillis());
			} else {
				MinDate = new Date(optimizationStart.getPlannedStarttime().getTime()
						- relation.getMinDifference().toMillis()
						- precedingAppointment.getBookedProcedure().getDuration().toMillis());
			}
			if(precedingAppointment.getPlannedStarttime() == null
					|| MinDate.before(precedingAppointment.getPlannedStarttime())){
				precedingAppointment.setPlannedStarttime(MinDate);
				this.optimizeAppointmentsForLatestBeginning(appointmentService, resourceService, employeeService, precedingAppointment);
			}
		}

		for(ProcedureRelation relation : optimizationStart.getBookedProcedure().getSubsequentRelations()){
			Appointment subsequentAppointment = this.getOrCreateAppointmentWithProcedure(relation.getProcedure(), optimizationStart.getBookedCustomer());

			Date MinDate;
			if (relation.getMinDifference() != null) {
				MinDate = new Date(relation.getMinDifference().toMillis()
						+ optimizationStart.getPlannedEndtime().getTime());
			} else {
				MinDate = optimizationStart.getPlannedEndtime();
			}
			if (subsequentAppointment.getStatus() != AppointmentStatus.RECOMMENDED) {
				subsequentAppointment.setPlannedStarttime(MinDate);
				this.optimizeAppointmentsForEarliestEnd(appointmentService, resourceService, employeeService, subsequentAppointment);
			}

			if (relation.getMaxDifference() != null) {
				Date MaxDate = new Date(optimizationStart.getPlannedEndtime().getTime()
						+ relation.getMaxDifference().toMillis());
				if(subsequentAppointment.getPlannedStarttime().after(MaxDate)){
					subsequentAppointment.setPlannedStarttime(MaxDate);
					this.optimizeAppointmentsForLatestBeginning(appointmentService, resourceService, employeeService, subsequentAppointment);
				}
			}
		}
	}
}

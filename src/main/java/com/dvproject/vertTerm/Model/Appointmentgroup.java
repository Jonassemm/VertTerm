package com.dvproject.vertTerm.Model;

import java.util.*;
import java.util.stream.Collectors;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.dvproject.vertTerm.Service.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;

import com.dvproject.vertTerm.exception.ProcedureException;
import com.dvproject.vertTerm.exception.ProcedureRelationException;
import com.dvproject.vertTerm.repository.UserRepository;
import com.dvproject.vertTerm.util.OverrideBooker;

/**
 * Everything not otherwise specified by Robert Schulz
 * @author Joshua Müller
 */
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
		return appointments.stream()
				.noneMatch(Appointment::hasId);
	}

	public boolean hasAllAppointmentidsSet() {
		return appointments.stream()
				.allMatch(Appointment::hasId);
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

	public boolean hasDistinctProcedures() {
		return appointments.stream()
						.map(app -> app.getBookedProcedure().getId())
						.distinct()
						.count() == appointments.size();
	}

	public void changeBookedCustomer(User bookedCustomer) {
		if (!bookedCustomer.getSystemStatus().isActive())
			throw new IllegalArgumentException("User is not active");

		appointments.forEach(app -> app.setBookedCustomer(bookedCustomer));
	}

	public void testWarnings(AppointmentServiceImpl appointmentService, RestrictionService restrictionService, UserRepository userRepository) {
		new OverrideBooker(this).bookable(appointmentService, restrictionService, userRepository);
	}

	/**
	 * Tests, whether a given user is allowed to book the appointment of the appointmentgroup
	 * i.e. all appointments have public procedures or at least one procedure is private and the user is an employee
	 */
	public void canBookProcedures(User user) {
		List<Procedure> procedures = appointments
										.stream()
										.map(Appointment::getBookedProcedure)
										.collect(Collectors.toList());
		Procedure privateProcedure = procedures.stream()
										.filter(Procedure::isPublicProcedure)
										.findAny()
										.orElse(null);
		boolean isEmployee = user != null && user instanceof Employee;
		boolean notBookable = !isEmployee && privateProcedure != null;

		if (notBookable)
			throw new ProcedureException(
					"At least one appointment contains a non-public procedure that a non-employee tries to book",
					privateProcedure);
	}

	/**
	 * Tests the procedure relations of the appointments
	 * if override is true and there is a conflict in the relations, then a warning is added to the appointment
	 * if override is false and there is a conflict in the relations, then an exception is thrown
	 */
	public void testProcedureRelations(boolean override) {
		// procedure.id -> appointment
		Map<String, Appointment> appointmentsMap = new HashMap<>();
		// procedure.id -> procedure
		Map<String, Procedure> proceduresMap = new HashMap<>();

		List<Appointment> appointments = this.appointments.stream()
							.filter(app -> app.getStatus() != AppointmentStatus.DELETED)
							.collect(Collectors.toList());

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
		} else 
			if (pre != null)
				throw pre;
	}

	/**
	 * Test method of {@link #testProcedureRelations} that determines whether the appointmentgroup contains
	 * all needed appointments to satisfy the procedure relations of the given appointment
	 */
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

	/**
	 * @author Robert Schulz
	 *
	 * Finds the last appointment in this group
	 */
	private Appointment findLatestAppointment(){
		Appointment result = this.appointments.get(0);
		for(Appointment appointment : this.getAppointments()){
			if(appointment.getPlannedEndtime().after(result.getPlannedEndtime())){
				result = appointment;
			}
		}
		return result;
	}

	/**
	 * @author Robert Schulz
	 *
	 * Finds the earliest Appointment in this group
	 */
	private Appointment findEarliestAppointment(){
		Appointment result = this.appointments.get(0);
		for(Appointment appointment : this.getAppointments()){
			if(appointment.getPlannedEndtime().before(result.getPlannedEndtime())){
				result = appointment;
			}
		}
		return result;
	}

	/**
	 * @author Robert Schulz
	 *
	 * Checks if all appointments have the booked entities defines by its procedure.
	 */
	private boolean allNeededBookablesFound() {
		for (Appointment appointment : this.getAppointments()){
			if(!appointment.allNeededBookablesFound()){
				return false;
			}
		}
		return true;
	}

	/**
	 * @author Robert Schulz
	 *
	 * Finds an Appointment in this group with the procedure or creates a new one.
	 */
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

	/**
	 * @author Robert Schulz
	 *
	 * Counts the amount of days needed between two appointments
	 */
	private int DaysNeededForAppointments(Appointment appointment1, Appointment appointment2){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(appointment1.getPlannedStarttime());
		int date1 = calendar.get(Calendar.DATE);
		calendar.setTime(appointment2.getPlannedEndtime());
		int date2 = calendar.get(Calendar.DATE);
		return date2 - date1;
	}

	/**
	 * @author Robert Schulz
	 *
	 * Generates a recommendation for each appointment to end the appointments on as few different days as possible.
	 * Because of performance reasons only interates 10 times.
	 */
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

	/**
	 * @author Robert Schulz
	 *
	 * Generates a recommendation for each appointment to end the appointments with as few Waiting time
	 * between Appointments as possiblie.
	 * Because of performance reasons calls optimizeForEarliestEnd and -LateBeginning.
	 */
	public void optimizeForLeastWaitingTime(AppointmentService appointmentService, ResourceService resourceService, EmployeeService employeeService, Appointment optimizationStart){
		this.optimizeAppointmentsForEarliestEnd(appointmentService, resourceService, employeeService, optimizationStart);
		for (Appointment appointment : this.getAppointments()) {
			appointment.setStatus(AppointmentStatus.OPEN);
		}
		this.optimizeAppointmentsForLatestBeginning(appointmentService, resourceService, employeeService, this.findLatestAppointment());
	}

	/**
	 * @author Robert Schulz
	 *
	 * Generates a recommendation for each appointment to end as early as possible after the specified times.
	 */
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

	/**
	 * @author Robert Schulz
	 *
	 * Generates a recommendation for each appointment to end as late as possible ahead of the specified times
	 */
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

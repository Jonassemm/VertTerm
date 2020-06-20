package com.dvproject.vertTerm.Model;

import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;

import com.dvproject.vertTerm.Service.AppointmentServiceImpl;
import com.dvproject.vertTerm.Service.RestrictionService;
import com.dvproject.vertTerm.exception.AppointmentException;
import com.dvproject.vertTerm.exception.AppointmentTimeException;
import com.dvproject.vertTerm.exception.EmployeeException;
import com.dvproject.vertTerm.exception.PositionException;
import com.dvproject.vertTerm.exception.ProcedureException;
import com.dvproject.vertTerm.exception.ResourceException;
import com.dvproject.vertTerm.exception.ResourceTypeException;
import com.dvproject.vertTerm.exception.RestrictionException;

public class Appointment implements Serializable {
	private static final long serialVersionUID = 2862268218236152790L;

	@Id
	private String id;
	private String description;
	@NotNull
	private AppointmentStatus status;
	@NotNull
	private List<Warning> warnings = new ArrayList<>();
	private boolean customerIsWaiting = false;

	private Date plannedStarttime;
	private Date plannedEndtime;
	private Date actualStarttime;
	private Date actualEndtime;

	@DBRef
	private Procedure bookedProcedure;
	@DBRef
	@NotNull
	private User bookedCustomer;
	@DBRef
	private List<Employee> bookedEmployees;
	@DBRef
	private List<Resource> bookedResources;

	public Appointment() {
		this.bookedResources = new ArrayList<>();
		this.bookedEmployees = new ArrayList<>();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public boolean hasId() {
		return id != null;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public AppointmentStatus getStatus() {
		return status;
	}

	public void setStatus(AppointmentStatus status) {
		this.status = status;
	}

	public Date getPlannedEndtime() {
		return plannedEndtime;
	}

	public Date generatePlannedEndtime() {
		Calendar enddate = Calendar.getInstance();
		enddate.setTime(this.plannedStarttime);
		int proceduredDurationInMinutesInt = 0;
		long procedureDurationInMinutesLong = bookedProcedure.getDuration().toMinutes();

		do {
			proceduredDurationInMinutesInt = procedureDurationInMinutesLong > Integer.MAX_VALUE ? Integer.MAX_VALUE
					: (int) procedureDurationInMinutesLong;
			enddate.add(Calendar.MINUTE, proceduredDurationInMinutesInt);
			procedureDurationInMinutesLong -= Integer.MAX_VALUE;
		} while (procedureDurationInMinutesLong > 0);

		return enddate.getTime();
	}

	public void setPlannedEndtime(Date plannedEndtime) {
		this.plannedEndtime = plannedEndtime;
	}

	public Date getPlannedStarttime() {
		return plannedStarttime;
	}

	public void setPlannedStarttime(Date plannedStarttime) {
		this.plannedStarttime = plannedStarttime;
	}

	public Date getActualStarttime() {
		return actualStarttime;
	}

	public void setActualStarttime(Date actualStarttime) {
		this.actualStarttime = actualStarttime;
	}

	public Date getActualEndtime() {
		return actualEndtime;
	}

	public void setActualEndtime(Date actualEndtime) {
		this.actualEndtime = actualEndtime;
	}

	public Procedure getBookedProcedure() {
		return bookedProcedure;
	}

	public void setBookedProcedure(Procedure bookedProcedure) {
		this.bookedProcedure = bookedProcedure;
	}

	public User getBookedCustomer() {
		return bookedCustomer;
	}

	public void setBookedCustomer(User bookedCustomers) {
		this.bookedCustomer = bookedCustomers;
	}

	public List<Employee> getBookedEmployees() {
		return bookedEmployees;
	}

	public void setBookedEmployees(List<Employee> bookedEmployees) {
		this.bookedEmployees = bookedEmployees;
	}

	public List<Resource> getBookedResources() {
		return bookedResources;
	}

	public void setBookedResources(List<Resource> bookedResources) {
		this.bookedResources = bookedResources;
	}

	public List<Warning> getWarnings() {
		return warnings;
	}

	public void setWarnings(List<Warning> warnings) {
		this.warnings = warnings;
	}

	public boolean addWarning(Warning... warnings) {
		boolean warningHasBeenAdded = false;

		for (Warning warning : warnings) {
			if (!this.warnings.contains(warning)) {
				this.warnings.add(warning);
				warningHasBeenAdded = true;
			}
		}

		return warningHasBeenAdded;
	}

	public boolean removeWarning(Warning warning) {
		if (warnings.contains(warning)) {
			return warnings.remove(warning);
		}

		return false;
	}

	public boolean isCustomerIsWaiting() {
		return customerIsWaiting;
	}

	public void setCustomerIsWaiting(boolean customerIsWaiting) {
		this.customerIsWaiting = customerIsWaiting;
	}

	public void testBlockage(AppointmentServiceImpl appointmentService) {
		testEmployeeAppointments(appointmentService);
		testResourceAppointments(appointmentService);
		testCustomerAppointments(appointmentService);
	}

	public void testOverlapping(List<TimeInterval> timeIntervallsOfAppointments) {
		boolean errorOccured = timeIntervallsOfAppointments.stream()
				.anyMatch(interval -> interval.isInTimeInterval(plannedStarttime, plannedEndtime));

		timeIntervallsOfAppointments.add(new TimeInterval(plannedStarttime, plannedEndtime));

		if (errorOccured)
			throw new AppointmentTimeException("An appointment overlaps with annother appointment", this);
	}

	public void testRestrictions(RestrictionService restrictionService) {
		List<Restriction> restrictionsToTest;
		List<Restriction> userRestrictions = bookedCustomer.getRestrictions();

		// test restrictions of procedure
		restrictionsToTest = bookedProcedure.getRestrictions();
		if (restrictionsToTest != null && !restrictionService.testRestrictions(restrictionsToTest, userRestrictions)) {
			throw new RestrictionException(
					"The appointment for the procedure contains a restriction that the given user also has", null);
		}

		for (Resource resource : bookedResources) {
			restrictionsToTest = resource.getRestrictions();

			// resource and user contain the same restriction
			if (restrictionsToTest != null
					&& !restrictionService.testRestrictions(restrictionsToTest, userRestrictions)) {
				throw new RestrictionException("A resource contains a restriction that the user also has", null);
			}
		}
	}

	public void testPlannedTimes() {
		if (plannedStarttime.after(plannedEndtime)) {
			throw new AppointmentTimeException("The planned starttime is after the planned endtime", this);
		}
		if (plannedStarttime.before(Date.from(Instant.now()))) {
			throw new AppointmentTimeException("The planned starttime is in the past", this);
		}
	}

	public void testAvailabilitiesOfProcedure_Employees_Resources() {
		testAvailabilitiesOfProcedur();
		testAvailabilitiesOfEmployees();
		testAvailabilitiesOfResources();
	}

	public void testAvailabilitiesOfProcedur() {
		bookedProcedure.isAvailable(plannedStarttime, plannedEndtime);
	}

	public void testAvailabilitiesOfEmployees() {
		bookedEmployees.forEach(emp -> emp.isAvailable(plannedStarttime, plannedEndtime));
	}

	public void testAvailabilitiesOfResources() {
		bookedResources.forEach(res -> res.isAvailable(plannedStarttime, plannedEndtime));
	}

	public void testBookedEmployeesAgainstPositionsOfProcedure() {
		boolean testVal = false;
		List<Position> procedurePositions = bookedProcedure.getNeededEmployeePositions();

		for (int i = 0; i < bookedEmployees.size(); i++) {
			Employee employee = bookedEmployees.get(i);
			Position positionOfProcedure = procedurePositions.get(i);

			List<Position> positionsOfEmployee = employee.getPositions();

			for (Position position : positionsOfEmployee) {
				if (position.getId().equals(positionOfProcedure.getId())) {
					testVal = true;
					break;
				}
			}

			if (!testVal)
				throw new PositionException("Missing employee for position", positionOfProcedure);

			testVal = false;
		}
	}

	public void testEmployeesOfAppointment() {
		if (this.bookedEmployees.size() != this.getBookedProcedure().getNeededEmployeePositions().size()) {
			throw new EmployeeException("Missing employees", null);
		}
	}

	public void testBookedResourcesAgainstResourceTypesOfProcedure() {
		List<ResourceType> procedureResourceTypes = bookedProcedure.getNeededResourceTypes();

		for (int i = 0; i < bookedResources.size(); i++) {
			boolean testVal = false;
			Resource resource = bookedResources.get(i);
			ResourceType resourceTypeOfProcedure = procedureResourceTypes.get(i);

			List<ResourceType> resourceTypesOfEmployee = resource.getResourceTypes();

			testVal = resourceTypesOfEmployee.stream()
					.anyMatch(resourcetype -> resourcetype.getId().equals(resourceTypeOfProcedure.getId()));

			if (!testVal)
				throw new ResourceTypeException("Missing resource for position", resourceTypeOfProcedure);
		}
	}

	public void testResourcesOfAppointment() {
		if (bookedResources.size() != getBookedProcedure().getNeededResourceTypes().size()) {
			throw new ResourceException("Missing resources", null);
		}
	}

	public void testProcedureDuration() {
		Duration appointmentDuration = Duration.between(plannedStarttime.toInstant(), plannedEndtime.toInstant());
		Duration procedureDuration = bookedProcedure.getDuration();

		if (procedureDuration != null && appointmentDuration.toSeconds() != procedureDuration.toSeconds()) {
			throw new ProcedureException("Duration of the appointment does not conform to the procedure ",
					bookedProcedure);
		}
	}

	public void testDistinctBookedAttributes() {
		hasDistinctEmployees();
		hasDistinctResources();
		hasDistinctBookedCustomer();
	}

	public void hasDistinctResources() {
		Resource resource = getNotDistinctAvailable(bookedResources);
		if (resource != null)
			throw new ResourceException("The same resource is beeing used twice: ", resource);
	}

	public void hasDistinctEmployees() {
		Employee employee = getNotDistinctAvailable(bookedEmployees);
		if (employee != null)
			throw new EmployeeException("The same employee is beeing used twice: ", employee);
	}

	public <T extends Available> T getNotDistinctAvailable(List<T> availables) {
		List<String> availableIds = new ArrayList<>();

		for (T available : availables) {
			String id = available.getId();
			if (!availableIds.contains(id)) {
				availableIds.add(id);
			} else {
				return available;
			}
		}

		return null;
	}

	public void hasDistinctBookedCustomer() {
		String bookedCustomerid = bookedCustomer.getId();
		if (bookedEmployees.stream().anyMatch(emp -> emp.getId().equals(bookedCustomerid))) {
			throw new EmployeeException("The bookedCustomer can not be a used employee at the same time", null);
		}
	}

	private void testEmployeeAppointments(AppointmentServiceImpl appointmentService) {
		List<Appointment> appointmentsOfEmployee = null;

		for (Employee employee : bookedEmployees) {
			String employeeid = employee.getId();
			// all appointments where the employee takes part to fullfill a position
			appointmentsOfEmployee = appointmentService.getAppointmentsOfBookedEmployeeInTimeinterval(employeeid,
					plannedStarttime, plannedEndtime, AppointmentStatus.PLANNED);
			// all appointments where the employee is the bookedCustomer
			appointmentsOfEmployee.addAll(appointmentService.getAppointmentsOfBookedCustomerInTimeinterval(employeeid,
					plannedStarttime, plannedEndtime, AppointmentStatus.PLANNED));

			if (isNotEmptyAndDoesNotOnlyContainOnlyThisAppointment(appointmentsOfEmployee)) {
				throw new AppointmentException("An employee already has an appointment in the given time interval",
						this);
			}
		}
	}

	private void testResourceAppointments(AppointmentServiceImpl appointmentService) {
		for (Resource resource : bookedResources) {
			List<Appointment> appointmentsOfResource = appointmentService.getAppointmentsOfBookedResourceInTimeinterval(
					resource.getId(), plannedStarttime, plannedEndtime, AppointmentStatus.PLANNED);

			if (isNotEmptyAndDoesNotOnlyContainOnlyThisAppointment(appointmentsOfResource)) {
				throw new AppointmentException("A resource already has an appointment in the given time interval",
						this);
			}
		}
	}

	private void testCustomerAppointments(AppointmentServiceImpl appointmentService) {
		try {
			List<Appointment> appointmentsOfCustomer = appointmentService.getAppointmentsOfBookedCustomerInTimeinterval(
					bookedCustomer.getId(), plannedStarttime, plannedEndtime, AppointmentStatus.PLANNED);

			if (isNotEmptyAndDoesNotOnlyContainOnlyThisAppointment(appointmentsOfCustomer)) {
				throw new AppointmentException("The customer already has an appointment in the given time interval",
						this);
			}
		} catch (NullPointerException ex) {
			if (!bookedCustomer.getUsername().contains("anonymousUser"))
				throw ex;
		}
	}

	private boolean isNotEmptyAndDoesNotOnlyContainOnlyThisAppointment(List<Appointment> appointments) {
		return !appointments.isEmpty() && (appointments.size() != 1 || !appointments.get(0).getId().equals(id));
	}
}

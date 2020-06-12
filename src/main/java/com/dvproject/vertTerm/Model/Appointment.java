package com.dvproject.vertTerm.Model;

import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
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

	public void addWarning(Warning warning) {
		if (!warnings.contains(warning)) {
			warnings.add(warning);
		}
	}

	public void removeWarning(Warning warning) {
		if (warnings.contains(warning)) {
			warnings.remove(warning);
		}
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
		boolean errorOccured = false;
		
		for (TimeInterval timeinterval : timeIntervallsOfAppointments) {
			if (timeinterval.isInTimeInterval(plannedStarttime, plannedEndtime)) {
				errorOccured = true;
				break;
			}
		}

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
		// test avilability of procedure
		bookedProcedure.isAvailable(plannedStarttime, plannedEndtime);

		for (Resource resource : bookedResources) {
			// test availability of resources
			resource.isAvailable(plannedStarttime, plannedEndtime);

		}

		for (Employee employee : bookedEmployees) {
			// test availability of employees
			employee.isAvailable(plannedStarttime, plannedEndtime);
		}
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
		boolean testVal = false;
		List<ResourceType> procedureResourceTypes = bookedProcedure.getNeededResourceTypes();

		for (int i = 0; i < bookedResources.size(); i++) {
			Resource resource = bookedResources.get(i);
			ResourceType resourceTypeOfProcedure = procedureResourceTypes.get(i);

			List<ResourceType> resourceTypesOfEmployee = resource.getResourceTypes();

			for (ResourceType resourceType : resourceTypesOfEmployee) {
				if (resourceType.getId().equals(resourceTypeOfProcedure.getId())) {
					testVal = true;
					break;
				}
			}

			if (!testVal)
				throw new ResourceTypeException("Missing resource for position", resourceTypeOfProcedure);

			testVal = false;
		}
	}

	public void testResourcesOfAppointment() {
		if (this.bookedResources.size() != this.getBookedProcedure().getNeededResourceTypes().size()) {
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

	void testDistinctBookedAttributes() {
		hasDistinctEmployees();
		hasDistinctResources();
	}

	public void hasDistinctResources() {
		List<String> resourceIds = new ArrayList<>();

		for (Resource resource : this.bookedResources) {
			String id = resource.getId();
			if (!resourceIds.contains(id)) {
				resourceIds.add(id);
			} else {
				throw new ResourceException("The same resource is beeing used twice: ", resource);
			}
		}
	}

	public void hasDistinctEmployees() {
		List<String> employeeIds = new ArrayList<>();

		for (Employee employee : this.bookedEmployees) {
			String id = employee.getId();
			if (!employeeIds.contains(id)) {
				employeeIds.add(id);
			} else {
				throw new EmployeeException("The same employee is beeing used twice: ", employee);
			}
		}
	}

	private void testEmployeeAppointments(AppointmentServiceImpl appointmentService) {
		for (Employee employee : bookedEmployees) {
			List<Appointment> appointmentsOfEmployeeAtThisAppointmentPlannedTimes = appointmentService
					.getAppointmentsOfBookedEmployeeInTimeinterval(employee.getId(), plannedStarttime, plannedEndtime,
							AppointmentStatus.PLANNED);

			if (!appointmentsOfEmployeeAtThisAppointmentPlannedTimes.isEmpty()) {
				throw new AppointmentException("An employee already has an appointment in the given time interval",
						this);
			}
		}
	}

	private void testResourceAppointments(AppointmentServiceImpl appointmentService) {
		for (Resource resource : bookedResources) {
			List<Appointment> appointmentsOfResourceAtThisAppointmentPlannedTimes = appointmentService
					.getAppointmentsOfBookedResourceInTimeinterval(resource.getId(), plannedStarttime, plannedEndtime,
							AppointmentStatus.PLANNED);

			if (!appointmentsOfResourceAtThisAppointmentPlannedTimes.isEmpty()) {
				throw new AppointmentException("A resource already has an appointment in the given time interval",
						this);
			}
		}
	}

	private void testCustomerAppointments(AppointmentServiceImpl appointmentService) {
		List<Appointment> appointmentsOfCustomerAtThisAppointmentPlannedTimes = appointmentService
				.getAppointmentsOfBookedCustomerInTimeinterval(bookedCustomer.getId(), plannedStarttime, plannedEndtime,
						AppointmentStatus.PLANNED);

		if (!appointmentsOfCustomerAtThisAppointmentPlannedTimes.isEmpty()) {
			throw new AppointmentException("The customer already has an appointment in the given time interval",
					this);
		}
	}

}

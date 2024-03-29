package com.dvproject.vertTerm.Model;

import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.stream.Collectors;

import javax.validation.constraints.NotNull;

import com.dvproject.vertTerm.Service.*;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import com.dvproject.vertTerm.exception.AppointmentTimeException;
import com.dvproject.vertTerm.exception.AppointmentInternalException;
import com.dvproject.vertTerm.exception.EmployeeException;
import com.dvproject.vertTerm.exception.PositionException;
import com.dvproject.vertTerm.exception.ProcedureException;
import com.dvproject.vertTerm.exception.ResourceException;
import com.dvproject.vertTerm.exception.ResourceTypeException;
import com.dvproject.vertTerm.exception.RestrictionException;

/**
 * Everything not otherwise specified by Robert Schulz
 * @author Joshua Müller
 */
@Document("appointment")
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
	private User bookedCustomer;
	@DBRef
	private List<Employee> bookedEmployees;
	@DBRef
	private List<Resource> bookedResources;

	public Appointment () {
		this.bookedResources = new ArrayList<>();
		this.bookedEmployees = new ArrayList<>();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public boolean hasSameIdAs(String appointmentid) {
		return id.equals(appointmentid);
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

	/**
	 * @author Joshua Müller
	 * 
	 * Generates the new plannedEndtime of this appointment when trying to pull it forward in time
	 */
	public Date generatePlannedEndtime(Date starttime) {
		Calendar enddate = Calendar.getInstance(TimeZone.getDefault());
		int proceduredDurationInMinutesInt = 0;
		Duration duration = bookedProcedure.getDuration();
		duration = duration == null ? Duration.between(this.plannedStarttime.toInstant(), this.plannedEndtime.toInstant())
				: duration;
		long procedureDurationInMinutesLong = duration.toMinutes();

		enddate.setTime(starttime);

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

	public void generateNewDatesFor(Date starttime) {
		plannedEndtime   = generatePlannedEndtime(starttime);
		plannedStarttime = starttime;
	}

	public Appointment getAppointmentWithNewDatesFor(Date starttime) {
		generateNewDatesFor(starttime);
		return this;
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

	public boolean ended() {
		return actualStarttime != null && actualEndtime != null;
	}

	public boolean started() {
		return actualStarttime != null && actualEndtime == null;
	}

	public boolean notStartedOrEnded() {
		return actualStarttime != null || actualEndtime != null;
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

	public List<Employee> retrieveNotActiveEmployees() {
		return bookedEmployees.stream()
					.filter(employee -> !employee.getSystemStatus().isActive())
					.collect(Collectors.toList());
	}

	public List<ObjectId> idsOfEmployees(){
		return bookedEmployees.stream().map(emp -> new ObjectId(emp.getId())).collect(Collectors.toList());
	}

	public void setBookedEmployees(List<Employee> bookedEmployees) {
		this.bookedEmployees = bookedEmployees;
	}

	public List<Resource> getBookedResources() {
		return bookedResources;
	}

	public List<Resource> retrieveNotActiveResources() {
		return bookedResources.stream()
					.filter(resource -> !resource.getStatus().isActive())
					.collect(Collectors.toList());
	}

	public List<ObjectId> idsOfResources () {
		return bookedResources.stream()
					.map(res -> new ObjectId(res.getId()))
					.collect(Collectors.toList());
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

	public boolean addWarnings(Warning... warnings) {
		boolean warningAdded = false;

		for (Warning warning : warnings) {
			warningAdded |= addWarning(warning);
		}

		return warningAdded;
	}

	public boolean addWarning(Warning warning) {
		return !warnings.contains(warning) ? warnings.add(warning) : false;
	}

	public boolean removeWarning(Warning warning) {
		return warnings.contains(warning) ? warnings.remove(warning) : false;
	}

	public boolean isCustomerIsWaiting() {
		return customerIsWaiting;
	}

	public void setCustomerIsWaiting(boolean customerIsWaiting) {
		this.customerIsWaiting = customerIsWaiting;
	}

	/**
	 * @author Joshua Müller
	 * 
	 * Tests whether the planned times in this appointment overlap with any TimeInterval in the list.
	 * If it does, then two of the appointments in the appointmentgroup that is being tested overlap.
	 * see AppointmentTester
	 */
	public void testOverlapping(List<TimeInterval> timeIntervallsOfAppointments) {
		boolean errorOccured = timeIntervallsOfAppointments.stream()
				.anyMatch(interval -> interval.isInTimeInterval(plannedStarttime, plannedEndtime));

		timeIntervallsOfAppointments.add(new TimeInterval(plannedStarttime, plannedEndtime));

		if (errorOccured)
			throw new AppointmentTimeException("An appointment overlaps with annother appointment", this);
	}

	/**
	 * @author Joshua Müller
	 * 
	 * Tests the appointment for restrictions of the resource/procedure that correspond to a restriction of the user
	 */
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
			if (restrictionsToTest != null && userRestrictions != null
					&& !restrictionService.testRestrictions(restrictionsToTest, userRestrictions))
				throw new RestrictionException(
						"The appointment for the procedure contains a restriction that the given user also has", null);
		}
	}

	public void testPlannedTimes() {
		if (plannedStarttime.after(plannedEndtime))
			throw new AppointmentTimeException("The planned starttime is after the planned endtime", this);

		if (plannedStarttime.before(Date.from(Instant.now())))
			throw new AppointmentTimeException("The planned starttime is in the past", this);
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

	/**
	 * @author Joshua Müller
	 * 
	 * Tests, whether the employees of the appointment satisfy the conditions of the positions of the procedure
	 */
	public void testBookedEmployeesAgainstPositionsOfProcedure() {
		List<Position> procedurePositions = bookedProcedure.getNeededEmployeePositions();

		if (procedurePositions.size() != bookedEmployees.size()) {
			// must be override -> set Warning
			addWarnings(Warning.EMPLOYEE_WARNING);
			return;
		}

		for (int i = 0; i < bookedEmployees.size(); i++) {
			boolean correctPositionsForEmployees = false;
			boolean positionsActive = false;
			Employee employee = bookedEmployees.get(i);
			Position positionOfProcedure = procedurePositions.get(i);

			List<Position> positionsOfEmployee = employee.getPositions();

			positionsActive              = positionsOfEmployee.stream()
					.allMatch(position -> position.getStatus().isActive());

			correctPositionsForEmployees = positionsOfEmployee.stream()
					.anyMatch(position -> position.sameAs(positionOfProcedure));

			if (!positionsActive)
				throw new PositionException("Position deleted", positionOfProcedure);

			if (!correctPositionsForEmployees)
				throw new PositionException("Missing employee for position", positionOfProcedure);
		}
	}

	public void testEmployeesOfAppointment() {
		if (bookedEmployees.size() != bookedProcedure.getNeededEmployeePositions().size())
			throw new EmployeeException("Missing employees", null);

		List<Employee> notActiveEmployees = retrieveNotActiveEmployees();

		if (notActiveEmployees.size() > 0)
			throw new EmployeeException("Employee of the appointment is not active", notActiveEmployees.get(0));
	}

	/**
	 * @author Joshua Müller
	 * 
	 * Tests, whether the resources of the appointment satisfy the conditions of the resourcetypes of the procedure
	 */
	public void testBookedResourcesAgainstResourceTypesOfProcedure() {
		List<ResourceType> procedureResourceTypes = bookedProcedure.getNeededResourceTypes();

		if (procedureResourceTypes.size() != bookedResources.size()) {
			// must be override -> set Warning
			addWarnings(Warning.RESOURCE_WARNING);
			return;
		}

		for (int i = 0; i < bookedResources.size(); i++) {
			boolean correctResourceTypes = false;
			boolean resourceTypesActive = false;
			Resource resource = bookedResources.get(i);
			ResourceType resourceTypeOfProcedure = procedureResourceTypes.get(i);
			List<ResourceType> resourceTypesOfEmployee = resource.getResourceTypes();

			resourceTypesActive  = resourceTypesOfEmployee.stream()
					.allMatch(resourcetype -> resourcetype.getStatus().isActive());

			correctResourceTypes = resourceTypesOfEmployee.stream()
					.anyMatch(resourcetype -> resourcetype.sameAs(resourceTypeOfProcedure));

			if (!resourceTypesActive)
				throw new ResourceTypeException("ResourceType deleted", resourceTypeOfProcedure);

			if (!correctResourceTypes)
				throw new ResourceTypeException("Missing resource for position", resourceTypeOfProcedure);
		}
	}

	public void testResourcesOfAppointment() {
		if (bookedResources.size() != bookedProcedure.getNeededResourceTypes().size())
			throw new ResourceException("Missing resources", null);

		List<Resource> notActiveResource = retrieveNotActiveResources();

		if (notActiveResource.size() > 0)
			throw new ResourceException("Resource of the appointment is not active", notActiveResource.get(0));
	}

	public void testProcedureDuration() {
		Duration appointmentDuration = Duration.between(plannedStarttime.toInstant(), plannedEndtime.toInstant());
		Duration procedureDuration = bookedProcedure.getDuration();

		if (procedureDuration != null && appointmentDuration.toSeconds() != procedureDuration.toSeconds())
			throw new ProcedureException("Duration of the appointment does not conform to the procedure ",
					bookedProcedure);

		if (!bookedProcedure.hasOnlyActiveEntities())
			throw new ProcedureException("Procedure needs to only contain active entities", bookedProcedure);
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

	/**
	 * 
	 * @author Joshua Müller
	 * 
	 * generic Method to test whether all availables (interface) are distinct
	 */
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
		if (bookedEmployees.stream()
					.anyMatch(emp -> emp.getId().equals(bookedCustomerid)))
			throw new EmployeeException("The bookedCustomer can not be a used employee at the same time", null);
	}

	/**
	 * 
	 * @author Joshua Müller
	 * 
	 * Tests, whether any other appointment with a resource, employee or user in the database overlaps with this one
	 */
	public void testBlockage(AppointmentService appointmentService) throws AppointmentInternalException {
		List<Appointment> failedAppointments = new ArrayList<>();
		String message = null;

		try {
			testEmployeeAppointments(appointmentService);
		} catch (AppointmentInternalException ex) {
			failedAppointments.addAll(ex.getAppointments());
			message = message != null ? message : ex.getMessage();
		}

		try {
			testResourceAppointments(appointmentService);
		} catch (AppointmentInternalException ex) {
			failedAppointments.addAll(ex.getAppointments());
			message = message != null ? message : ex.getMessage();
		}

		try {
			testCustomerAppointments(appointmentService);
		} catch (AppointmentInternalException ex) {
			failedAppointments.addAll(ex.getAppointments());
			message = message != null ? message : ex.getMessage();
		}

		if (failedAppointments.size() != 0)
			throw new AppointmentInternalException(failedAppointments, message, this);
	}

	/**
	 * 
	 * @author Joshua Müller
	 * 
	 * Tests, whether any other appointment with an employee in the database overlaps with this one
	 */
	private void testEmployeeAppointments(AppointmentService appointmentService) throws AppointmentInternalException {
		List<Appointment> failedAppointments = new ArrayList<>();
		List<Appointment> appointmentsOfEmployee = null;

		for (Employee employee : bookedEmployees) {
			String employeeid = employee.getId();
			// all appointments where the employee takes part to fullfill a position
			appointmentsOfEmployee = appointmentService.getAppointmentsOfBookedEmployeeInTimeinterval(employeeid,
					plannedStarttime, plannedEndtime, AppointmentStatus.PLANNED);
			// all appointments where the employee is the bookedCustomer
			appointmentsOfEmployee.addAll(appointmentService.getAppointmentsOfBookedCustomerInTimeinterval(employeeid,
					plannedStarttime, plannedEndtime, AppointmentStatus.PLANNED));

			if (isNotEmptyAndDoesNotOnlyContainThisAppointment(appointmentsOfEmployee))
				failedAppointments.addAll(
						appointmentsOfEmployee.stream()
									.filter(app -> !app.getId().equals(id))
									.collect(Collectors.toList()));
		}

		if (failedAppointments.size() != 0)
			throw new AppointmentInternalException(failedAppointments,
					"An employee already has an appointment in the given time interval", this);
	}

	/**
	 * 
	 * @author Joshua Müller
	 * 
	 * Tests, whether any other appointment with a resources in the database overlaps with this one
	 */
	private void testResourceAppointments(AppointmentService appointmentService) throws AppointmentInternalException {
		List<Appointment> failedAppointments = new ArrayList<>();

		for (Resource resource : bookedResources) {
			List<Appointment> appointmentsOfResource = appointmentService.getAppointmentsOfBookedResourceInTimeinterval(
					resource.getId(), plannedStarttime, plannedEndtime, AppointmentStatus.PLANNED);

			if (isNotEmptyAndDoesNotOnlyContainThisAppointment(appointmentsOfResource))
				failedAppointments.addAll(
						appointmentsOfResource.stream()
								.filter(app -> !app.getId().equals(id))
								.collect(Collectors.toList()));
		}

		if (failedAppointments.size() != 0)
			throw new AppointmentInternalException(failedAppointments,
					"A resource already has an appointment in the given time interval", this);
	}

	/**
	 * 
	 * @author Joshua Müller
	 * 
	 * Tests, whether any other appointment with the user in the database overlaps with this one
	 */
	private void testCustomerAppointments(AppointmentService appointmentService) throws AppointmentInternalException {
		try {
			List<Appointment> appointmentsOfCustomer = appointmentService.getAppointmentsOfBookedCustomerInTimeinterval(
					bookedCustomer.getId(), plannedStarttime, plannedEndtime, AppointmentStatus.PLANNED);

			if (isNotEmptyAndDoesNotOnlyContainThisAppointment(appointmentsOfCustomer)) {
				throw new AppointmentInternalException(
						appointmentsOfCustomer.stream()
								.filter(app -> !app.getId().equals(id))
								.collect(Collectors.toList()),
						"The customer already has an appointment in the given time interval", this);
			}
		} catch (NullPointerException ex) {
			if (!bookedCustomer.getUsername().contains("anonymousUser"))
				throw ex;
		}
	}

	private boolean isNotEmptyAndDoesNotOnlyContainThisAppointment(List<Appointment> appointments) {
		return !appointments.isEmpty() && (appointments.size() != 1 || !appointments.get(0).getId().equals(id));
	}
	
	/**
	 * @author Robert Schulz
	 */
	public boolean blocksBookedEntities() {
		return this.getStatus() == AppointmentStatus.DONE || this.getStatus() == AppointmentStatus.PLANNED;
	}

	/**
	 * @author Robert Schulz
	 *
	 * Tests if all needed entities defined in the procedure are present.
	 */
	public boolean allNeededBookablesFound() {
		for (Position needed : this.getBookedProcedure().getNeededEmployeePositions()) {
			boolean found = false;
			for (Employee present : this.getBookedEmployees()) {
				for (Position presentPosition : present.getPositions()) {
					if (presentPosition.equals(needed))
						found = true;
				}
			}
			if (!found) { return false; }
		}
		for (ResourceType needed : this.getBookedProcedure().getNeededResourceTypes()) {
			boolean found = false;
			for (Resource present : this.getBookedResources()) {
				for (ResourceType presentType : present.getResourceTypes()) {
					if (presentType.equals(needed))
						found = true;
				}
			}
			if (!found) { return false; }
		}
		return true;
	}

	/**
	 * @author Robert Schulz
	 *
	 * Searches for a date where all needed bookables of the procedure are found and populates them.
	 * It will use the first date after the defined PlannedStartTime.
	 */
	public void optimizeAndPopulateForEarliestEnd(AppointmentService appointmentService, ResourceService resourceService,
			EmployeeService employeeService) {
		if (this.getBookedProcedure() == null) { return; }
		if (this.getPlannedStarttime() == null) { return; }
		this.setStatus(AppointmentStatus.RECOMMENDED);
		this.setPlannedEndtime(
				new Date(this.plannedStarttime.getTime() + this.getBookedProcedure().getDuration().toMillis()));
		// check if customer is available
		this.getBookedCustomer().populateAppointments(appointmentService);
		Date newDateToEvaluate = this.getBookedCustomer().getEarliestAvailableDate(this.getPlannedStarttime(),
				this.getBookedProcedure().getDuration());

		List<Employee> oldEmployeeList = this.getBookedEmployees();
		List<Resource> oldResourceList = this.getBookedResources();

		this.setBookedEmployees(new ArrayList<>());
		this.setBookedResources(new ArrayList<>());

		// check for all resource types if there is one available. otherwise
		// do everything again with the earliest detected available date
		for (ResourceType resourceType : this.getBookedProcedure().getNeededResourceTypes()) {
			Date newDateToEvaluateForRessources = null;
			boolean resourceTypeFound = false;
			for (Resource resource : oldResourceList) {
				if (this.getBookedResources().contains(resource)) {
					continue;
				}
				resource.populateAppointments(appointmentService);
				Date dateForThisResource = resource.getEarliestAvailableDate(this.getPlannedStarttime(),
						this.getBookedProcedure().getDuration());
				if (dateForThisResource != null) {
					if (newDateToEvaluateForRessources == null) {
						newDateToEvaluateForRessources = dateForThisResource;
					}
					if (dateForThisResource.before(newDateToEvaluateForRessources)) {
						newDateToEvaluateForRessources = dateForThisResource;
					}
					if (!dateForThisResource.after(this.getPlannedStarttime())) {
						this.getBookedResources().add(resource);
						resourceTypeFound = true;
						break;
					}
				}
			}
			if (resourceTypeFound) {
				break;
			}
			for (Resource resource : resourceService.getAll(resourceType)) {
				if (this.getBookedResources().contains(resource)) {
					continue;
				}
				if (resource.getStatus() == Status.ACTIVE) {
					continue;
				}
				resource.populateAppointments(appointmentService);
				Date dateForThisResource = resource.getEarliestAvailableDate(this.getPlannedStarttime(),
						this.getBookedProcedure().getDuration());
				if (dateForThisResource != null) {
					if (newDateToEvaluateForRessources == null) {
						newDateToEvaluateForRessources = dateForThisResource;
					}
					if (dateForThisResource.before(newDateToEvaluateForRessources)) {
						newDateToEvaluateForRessources = dateForThisResource;
					}
					if (!dateForThisResource.after(this.getPlannedStarttime())) {
						this.getBookedResources().add(resource);
						resourceTypeFound = true;
						break;
					}
				}
			}
			if (resourceTypeFound) {
				break;
			}
			if (newDateToEvaluateForRessources == null) {
				// this ressource type has no possible ressource. I have to think about what to
				// do in this case.
			} else
				if (newDateToEvaluateForRessources.after(newDateToEvaluate)) {
					newDateToEvaluate = newDateToEvaluateForRessources;
				}
		}

		// do the same for roles
		for (Position position : this.getBookedProcedure().getNeededEmployeePositions()) {
			Date newDateToEvaluateForEmployees = null;
			boolean employeeFound = false;
			for (Employee employee : oldEmployeeList) {
				if (this.getBookedEmployees().contains(employee)) {
					continue;
				}
				employee.populateAppointments(appointmentService);
				Date dateForThisEmployee = employee.getEarliestAvailableDate(this.getPlannedStarttime(),
						this.getBookedProcedure().getDuration());
				if (dateForThisEmployee != null) {
					if (newDateToEvaluateForEmployees == null) {
						newDateToEvaluateForEmployees = dateForThisEmployee;
					}
					if (dateForThisEmployee.before(newDateToEvaluateForEmployees)) {
						newDateToEvaluateForEmployees = dateForThisEmployee;
					}
					if (!dateForThisEmployee.after(this.getPlannedStarttime())) {
						this.getBookedEmployees().add(employee);
						employeeFound = true;
						break;
					}
				}
			}
			if (employeeFound) {
				break;
			}
			for (Employee employee : employeeService.getAll(position.getId())) {
				if (this.getBookedEmployees().contains(employee)) {
					continue;
				}
				if (employee.getSystemStatus() != Status.ACTIVE) {
					continue;
				}
				employee.populateAppointments(appointmentService);
				Date dateForThisEmployee = employee.getEarliestAvailableDate(this.getPlannedStarttime(),
						this.getBookedProcedure().getDuration());
				if (dateForThisEmployee != null) {
					if (newDateToEvaluateForEmployees == null) {
						newDateToEvaluateForEmployees = dateForThisEmployee;
					}
					if (dateForThisEmployee.before(newDateToEvaluateForEmployees)) {
						newDateToEvaluateForEmployees = dateForThisEmployee;
					}
					if (!dateForThisEmployee.after(this.getPlannedStarttime())) {
						this.getBookedEmployees().add(employee);
						employeeFound = true;
						break;
					}
				}
			}
			if (employeeFound) {
				break;
			}
			if (newDateToEvaluateForEmployees == null) {
				// this position has no possible Employee. I have to think about what to do in
				// this case.
			} else
				if (newDateToEvaluateForEmployees.after(newDateToEvaluate)) {
					newDateToEvaluate = newDateToEvaluateForEmployees;
				}
		}

		if (newDateToEvaluate.after(this.getPlannedStarttime())) {
			this.setBookedResources(oldResourceList);
			this.setBookedEmployees(oldEmployeeList);
			this.setPlannedStarttime(newDateToEvaluate);
			this.optimizeAndPopulateForEarliestEnd(appointmentService, resourceService, employeeService);
		}
	}

	/**
	 * @author Robert Schulz
	 *
	 * Searches for a date where all needed bookables of the procedure are found and populates them.
	 * It will use the last date before the defined PlannedStartTime.
	 */
	public void optimizeAndPopulateForLatestBeginning(AppointmentService appointmentService,
			ResourceService resourceService, EmployeeService employeeService) {
		if (this.getBookedProcedure() == null) { return; }
		if (this.getPlannedStarttime() == null) { return; }
		this.setStatus(AppointmentStatus.RECOMMENDED);
		this.setPlannedEndtime(
				new Date(this.plannedStarttime.getTime() + this.getBookedProcedure().getDuration().toMillis()));
		// check if customer is available
		this.getBookedCustomer().populateAppointments(appointmentService);
		Date newDateToEvaluate = this.getBookedCustomer().getLatestAvailableDate(this.getPlannedStarttime(),
				this.getBookedProcedure().getDuration());
		List<Employee> oldEmployeeList = this.getBookedEmployees();
		List<Resource> oldResourceList = this.getBookedResources();

		this.setBookedEmployees(new ArrayList<>());
		this.setBookedResources(new ArrayList<>());

		// check for all resource types if there is one available. otherwise
		// do everything again with the earliest detected available date
		for (ResourceType resourceType : this.getBookedProcedure().getNeededResourceTypes()) {
			Date newDateToEvaluateForRessources = null;
			boolean resourceTypeFound = false;
			for (Resource resource : oldResourceList) {
				if (this.getBookedResources().contains(resource)) {
					continue;
				}
				resource.populateAppointments(appointmentService);
				Date temp = resource.getLatestAvailableDate(this.getPlannedStarttime(),
						this.getBookedProcedure().getDuration());
				if (temp != null) {
					if (newDateToEvaluateForRessources == null) {
						newDateToEvaluateForRessources = temp;
					}
					if (temp.after(newDateToEvaluateForRessources)) {
						newDateToEvaluateForRessources = temp;
					}
					if (!temp.before(this.getPlannedStarttime())) {
						this.getBookedResources().add(resource);
						resourceTypeFound = true;
						break;
					}
				}
			}
			if (resourceTypeFound) {
				break;
			}
			for (Resource resource : resourceService.getAll(resourceType)) {
				if (this.getBookedResources().contains(resource)) {
					continue;
				}
				if (resource.getStatus() == Status.ACTIVE) {
					continue;
				}
				resource.populateAppointments(appointmentService);
				Date dateForThisResource = resource.getLatestAvailableDate(this.getPlannedStarttime(),
						this.getBookedProcedure().getDuration());
				if (dateForThisResource != null) {
					if (newDateToEvaluateForRessources == null) {
						newDateToEvaluateForRessources = dateForThisResource;
					}
					if (dateForThisResource.after(newDateToEvaluateForRessources)) {
						newDateToEvaluateForRessources = dateForThisResource;
					}
					if (!dateForThisResource.before(this.getPlannedStarttime())) {
						this.getBookedResources().add(resource);
						resourceTypeFound = true;
						break;
					}
				}
			}
			if (resourceTypeFound) {
				break;
			}
			if (newDateToEvaluateForRessources == null) {
				// this ressource type has no possible ressource. I have to think about what to
				// do in this case.
			} else
				if (newDateToEvaluateForRessources.before(newDateToEvaluate)) {
					newDateToEvaluate = newDateToEvaluateForRessources;
				}
		}
		// do the same for roles
		for (Position position : this.getBookedProcedure().getNeededEmployeePositions()) {
			Date newDateToEvaluateForEmployees = null;
			boolean employeeFound = false;
			for (Employee employee : oldEmployeeList) {
				if (this.getBookedEmployees().contains(employee)) {
					continue;
				}
				employee.populateAppointments(appointmentService);
				Date dateForThisEmployee = employee.getLatestAvailableDate(this.getPlannedStarttime(),
						this.getBookedProcedure().getDuration());
				if (dateForThisEmployee != null) {
					if (newDateToEvaluateForEmployees == null) {
						newDateToEvaluateForEmployees = dateForThisEmployee;
					}
					if (dateForThisEmployee.after(newDateToEvaluateForEmployees)) {
						newDateToEvaluateForEmployees = dateForThisEmployee;
					}
					if (!dateForThisEmployee.before(this.getPlannedStarttime())) {
						this.getBookedEmployees().add(employee);
						employeeFound = true;
						break;
					}
				}
			}
			if (employeeFound) {
				break;
			}
			for (Employee employee : employeeService.getAll(position.getId())) {
				if (this.getBookedEmployees().contains(employee)) {
					continue;
				}
				if (employee.getSystemStatus() != Status.ACTIVE) {
					continue;
				}
				employee.populateAppointments(appointmentService);
				Date dateForThisEmployee = employee.getLatestAvailableDate(this.getPlannedStarttime(),
						this.getBookedProcedure().getDuration());
				if (dateForThisEmployee != null) {
					if (newDateToEvaluateForEmployees == null) {
						newDateToEvaluateForEmployees = dateForThisEmployee;
					}
					if (dateForThisEmployee.after(newDateToEvaluateForEmployees)) {
						newDateToEvaluateForEmployees = dateForThisEmployee;
					}
					if (!dateForThisEmployee.before(this.getPlannedStarttime())) {
						this.getBookedEmployees().add(employee);
						employeeFound = true;
						break;
					}
				}
			}
			if (employeeFound) {
				break;
			}
			if (newDateToEvaluateForEmployees == null) {
				// this position has no possible Employee. I have to think about what to do in
				// this case.
			} else
				if (newDateToEvaluateForEmployees.before(newDateToEvaluate)) {
					newDateToEvaluate = newDateToEvaluateForEmployees;
				}
		}

		if (newDateToEvaluate.before(this.getPlannedStarttime())) {
			this.setBookedResources(oldResourceList);
			this.setBookedEmployees(oldEmployeeList);
			this.setPlannedStarttime(newDateToEvaluate);
			this.optimizeAndPopulateForLatestBeginning(appointmentService, resourceService, employeeService);
		}
	}
}

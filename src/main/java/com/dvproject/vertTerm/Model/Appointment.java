package com.dvproject.vertTerm.Model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;

import com.dvproject.vertTerm.exception.PositionException;
import com.dvproject.vertTerm.exception.ResourceTypeException;

public class Appointment implements Serializable {
	private static final long serialVersionUID = 2862268218236152790L;

	@Id
	private String id;
	private String description;
	@NotNull
	private AppointmentStatus status;
	@NotNull
	private Warning warning;

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

	public Warning getWarning() {
		return warning;
	}

	public void setWarning(Warning warning) {
		this.warning = warning;
	}

	public void isBookable() {
		testBookedEmployeesAgainstPositionsOfProcedure();
		testBookedResourcesAgainstResourceTypesOfProcedure();
		
		//test avilability of procedure
		bookedProcedure.isAvailable(plannedStarttime, plannedEndtime);
		
		for (Resource resource : bookedResources) {
			//test availability of resource
			for (Availability availability : resource.getAvailabilities()) {
				//test availabilities of resource
				availability.isAvailableBetween(plannedStarttime, plannedEndtime);
			}

		}
		
		for (Employee employee : bookedEmployees) {
			for (Availability availability : employee.getAvailabilities()) {
				//test availabilities of employee
				availability.isAvailableBetween(plannedStarttime, plannedEndtime);
			}
		}
	}

	private void testBookedEmployeesAgainstPositionsOfProcedure() {
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

	private void testBookedResourcesAgainstResourceTypesOfProcedure() {
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
}

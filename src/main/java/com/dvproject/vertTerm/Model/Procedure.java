package com.dvproject.vertTerm.Model;

import java.io.Serializable;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotNull;

import com.dvproject.vertTerm.Service.AppointmentService;
import com.dvproject.vertTerm.Service.AppointmentServiceImpl;
import com.dvproject.vertTerm.Service.EmployeeService;
import com.dvproject.vertTerm.Service.ResourceService;
import com.dvproject.vertTerm.exception.AvailabilityException;
import com.dvproject.vertTerm.exception.PositionException;
import com.dvproject.vertTerm.exception.ResourceTypeException;
import com.fasterxml.jackson.annotation.JsonFormat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;

public class Procedure implements Serializable, Available {
	private static final long serialVersionUID = 1758602258863163151L;

	@Autowired
	private ResourceService resourceService;
	@Autowired
	private EmployeeService employeeService;

	@Id
	private String id;
	@Indexed(unique = true)
	private String name;
	private String description;
	@JsonFormat(shape = JsonFormat.Shape.NUMBER)
	private Duration duration;
	private int pricePerInvocation;
	private int pricePerHour;
	@NotNull
	private Status status;
	private boolean publicProcedure;

	private List<ProcedureRelation> precedingRelations;
	private List<ProcedureRelation> subsequentRelations;

	@DBRef
	private List<ResourceType> neededResourceTypes;
	@DBRef
	private List<Position> neededEmployeePositions;
	@DBRef
	private List<Restriction> restrictions;
	@DBRef
	private List<Availability> availabilities;

	public Procedure(){
		this.neededEmployeePositions = new ArrayList<>();
		this.neededResourceTypes = new ArrayList<>();
		this.subsequentRelations = new ArrayList<>();
		this.precedingRelations = new ArrayList<>();
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Duration getDuration() {
		return duration;
	}

	public void setDuration(Duration duration) {
		this.duration = duration;
	}

	public int getPricePerInvocation() {
		return pricePerInvocation;
	}

	public void setPricePerInvocation(int pricePerInvocation) {
		this.pricePerInvocation = pricePerInvocation;
	}

	public int getPricePerHour() {
		return pricePerHour;
	}

	public void setPricePerHour(int pricePerHour) {
		this.pricePerHour = pricePerHour;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public boolean isPublicProcedure() {
		return publicProcedure;
	}

	public void setPublicProcedure(boolean publicProcedure) {
		this.publicProcedure = publicProcedure;
	}

	public List<ProcedureRelation> getPrecedingRelations() {
		return precedingRelations;
	}

	public void setPrecedingRelations(List<ProcedureRelation> precedingRelations) {
		precedingRelations.forEach(this::testProcedureRelation);

		this.precedingRelations = precedingRelations;
	}

	public List<ProcedureRelation> getSubsequentRelations() {
		return subsequentRelations;
	}

	public void setSubsequentRelations(List<ProcedureRelation> subsequentRelations) {
		subsequentRelations.forEach(this::testProcedureRelation);

		this.subsequentRelations = subsequentRelations;
	}

	public List<ResourceType> getNeededResourceTypes() {
		return neededResourceTypes;
	}

	public void setNeededResourceTypes(List<ResourceType> neededResourceTypes) {
		this.neededResourceTypes = neededResourceTypes;
	}

	public List<Position> getNeededEmployeePositions() {
		return neededEmployeePositions;
	}

	public void setNeededEmployeePositions(List<Position> neededEmployeePositions) {
		this.neededEmployeePositions = neededEmployeePositions;
	}

	public List<Restriction> getRestrictions() {
		return restrictions;
	}

	public void setRestrictions(List<Restriction> restrictions) {
		this.restrictions = restrictions;
	}

	public List<Availability> getAvailabilities() {
		return availabilities;
	}

	public void setAvailabilities(List<Availability> availabilities) {
		this.availabilities = availabilities;
	}

	public boolean hasOnlyActiveEntities() {
		boolean retVal = status == Status.ACTIVE;

		retVal = retVal && neededResourceTypes.stream().allMatch(entity -> entity.getStatus() == Status.ACTIVE);

		retVal = retVal && neededEmployeePositions.stream().allMatch(entity -> entity.getStatus() == Status.ACTIVE);

		retVal = retVal
				&& precedingRelations.stream().allMatch(entity -> entity.getProcedure().getStatus() == Status.ACTIVE);
		
		retVal = retVal
				&& subsequentRelations.stream().allMatch(entity -> entity.getProcedure().getStatus() == Status.ACTIVE);

		return retVal;
	}

	public void isAvailable(Date startdate, Date enddate) {
		if (!availabilities.stream().anyMatch(availability -> availability.isAvailableBetween(startdate, enddate)))
			throw new AvailabilityException("No availability for the procedure " + name);
	}

	public void testAllRelations() {
		precedingRelations.forEach(this::testProcedureRelation);
		subsequentRelations.forEach(this::testProcedureRelation);
	}

	@Override
	public List<Appointment> getAppointmentsAfterDate(AppointmentService appointmentService, Date startdate) {
		List<Appointment> appointments = appointmentService.getAppointmentsOf(this, startdate);

		appointments.forEach(appointment -> appointment.getBookedProcedure().setAvailabilities(availabilities));

		return appointments;
	}

	public void testAllReferenceValues() {
		testAllRelations();
		testPositions();
		testResourceTypes();
	}

	public void testResourceTypes() {
		ResourceType failedResourceType = neededResourceTypes.stream()
				.filter(resourceType -> resourceType.getStatus() == Status.DELETED).findAny().orElse(null);

		if (failedResourceType != null)
			throw new ResourceTypeException("ResourceType is deleted", failedResourceType);
	}

	public void testPositions() {
		Position failedposition = neededEmployeePositions.stream()
				.filter(position -> position.getStatus() == Status.DELETED).findAny().orElse(null);

		if (failedposition != null)
			throw new PositionException("Position is deleted", failedposition);
	}

	private void testProcedureRelation(ProcedureRelation procedureRelation) {
		if (procedureRelation.getProcedure().getId().equals(this.getId()))
			throw new IllegalArgumentException("Procedure can have no relation to itself");
	}
}

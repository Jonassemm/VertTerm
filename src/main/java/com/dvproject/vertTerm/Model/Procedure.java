package com.dvproject.vertTerm.Model;

import java.io.Serializable;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;

public class Procedure implements Serializable {
	private static final long serialVersionUID = 1758602258863163151L;
	
	@Id
	private String id;
	@Indexed(unique = true)
	private String name;
	private String description;
	private int durationInMinutes;
	private int pricePerInvocation;
	private int pricePerHour;
	@NotNull
	private Status status;
	
	@DBRef
	private List<ProcedureRelation> precedingRelations;
	@DBRef
	private List<ProcedureRelation> subsequentRelations;
	
	@DBRef
	private List<ResourceType> neededResourceTypes;
	@DBRef
	private List<Position> neededEmployeePositions;
	@DBRef
	private List<Restriction> restrictions;
	private List<Availability> availabilities;

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
	
	public int getDurationInMinutes() {
		return durationInMinutes;
	}
	
	public void setDurationInMinutes(int durationInMinutes) {
		this.durationInMinutes = durationInMinutes;
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
	
	public List<ProcedureRelation> getPrecedingRelations() {
		return precedingRelations;
	}
	
	public void setPrecedingRelations(List<ProcedureRelation> precedingRelations) {
		this.precedingRelations = precedingRelations;
	}
	
	public List<ProcedureRelation> getSubsequentRelations() {
		return subsequentRelations;
	}
	
	public void setSubsequentRelations(List<ProcedureRelation> subsequentRelations) {
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

}

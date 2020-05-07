package com.dvproject.vertTerm.Model;

import java.io.Serializable;
import java.util.List;

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
	private int pricePerHour;
	
	@DBRef
	private List<ProcedureRelation> possibleFollowingProcedures;
	@DBRef
	private List<ResourceType> neededResourceTypes;
	@DBRef
	private List<Position> neededEmployeePositions;
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
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
	
	public int getPricePerHour() {
		return pricePerHour;
	}
	
	public void setPricePerHour(int pricePerHour) {
		this.pricePerHour = pricePerHour;
	}

	public List<ProcedureRelation> getPossibleFollowingProcedures() {
		return possibleFollowingProcedures;
	}

	public void setPossibleFollowingProcedures(List<ProcedureRelation> possibleFollowingProcedures) {
		this.possibleFollowingProcedures = possibleFollowingProcedures;
	}
}

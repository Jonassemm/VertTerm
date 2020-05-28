package com.dvproject.vertTerm.Model;

import java.time.Duration;

import javax.validation.constraints.NotNull;

import org.springframework.data.mongodb.core.mapping.DBRef;

import com.fasterxml.jackson.annotation.JsonFormat;

public class ProcedureRelation {
	@JsonFormat(shape = JsonFormat.Shape.NUMBER)
	private Duration minDifference;
	@JsonFormat(shape = JsonFormat.Shape.NUMBER)
	private Duration maxDifference;
	@DBRef
	@NotNull
	private Procedure procedure;
	
	public Duration getMinDifference() {
		return minDifference;
	}
	
	public void setMinDifference(Duration minDifference) {
		this.minDifference = minDifference;
	}
	
	public Duration getMaxDifference() {
		return maxDifference;
	}
	
	public void setMaxDifference(Duration maxDifference) {
		this.maxDifference = maxDifference;
	}
	
	public Procedure getProcedure() {
		return procedure;
	}
	
	public void setProcedure(Procedure procedure) {
		this.procedure = procedure;
	}

}

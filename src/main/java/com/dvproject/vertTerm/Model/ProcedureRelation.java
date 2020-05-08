package com.dvproject.vertTerm.Model;

import java.time.Duration;

import javax.validation.constraints.NotNull;

import org.springframework.data.mongodb.core.mapping.DBRef;

public class ProcedureRelation {
	
	private Duration minDifference;
	private Duration maxDifference;
	@DBRef
	@NotNull
	private Procedure followingProcedure;
	
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
	
	public Procedure getFollowingProcedure() {
		return followingProcedure;
	}
	
	public void setFollowingProcedure(Procedure followingProcedure) {
		this.followingProcedure = followingProcedure;
	}

}

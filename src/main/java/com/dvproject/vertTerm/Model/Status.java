package com.dvproject.vertTerm.Model;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum Status {
	/**
	 * can be used
	 */
	@JsonProperty("active")
	ACTIVE,
	
	/**
	 * can not be used for some time
	 */
	@JsonProperty("inactive")
	INACTIVE,
	
	/**
	 * can never be used again
	 */
	@JsonProperty("deleted")
	DELETED;
	
	public boolean isActive() {
		return this == Status.ACTIVE;
	}
	
	public boolean isInactive() {
		return this == Status.ACTIVE;
	}
	
	public boolean isDeleted() {
		return this == Status.ACTIVE;
	}
}

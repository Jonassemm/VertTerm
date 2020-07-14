package com.dvproject.vertTerm.Model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Joshua Müller
 */
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
		return this == ACTIVE;
	}
	
	public boolean isInactive() {
		return this == INACTIVE;
	}
	
	public boolean isDeleted() {
		return this == DELETED;
	}
}

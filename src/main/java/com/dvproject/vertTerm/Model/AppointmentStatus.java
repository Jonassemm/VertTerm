package com.dvproject.vertTerm.Model;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum AppointmentStatus {
	/**
	 * appointment has been completed, actual times has been set
	 */
	@JsonProperty("done")
	DONE,
	
	/**
	 * appointment target times have been set
	 */
	@JsonProperty("planned")
	PLANNED,
	
	/**
	 * appointment has been created and can be booked
	 */
	@JsonProperty("created")
	CREATED,
	
	/**
	 * appointment has been cancelled and can only be booked again by an employee with the needed rights
	 */
	@JsonProperty("cancelled")
	CANCELLED,
	
	/**
	 * appointment has been deactivated and can only be booked again by the customer himself
	 */
	@JsonProperty("deactivated")
	DEACTIVATED,
	
	/**
	 * appointment has been deleted and can no longer be used
	 */
	@JsonProperty("deleted")
	DELETED;
}

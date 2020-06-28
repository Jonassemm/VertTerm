package com.dvproject.vertTerm.Model;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum AppointmentStatus {
	/**
	 * appointment has been completed, actual times have been set
	 */
	@JsonProperty("done")
	DONE,
	
	/**
	 * appointment planned times have been set
	 */
	@JsonProperty("planned")
	PLANNED,

	/**
	 * appointment planned times have been set
	 */
	@JsonProperty("planned")
	RECOMMENDED,
	
	/**
	 * appointment has been deleted and can no longer be used
	 */
	@JsonProperty("deleted")
	DELETED
}

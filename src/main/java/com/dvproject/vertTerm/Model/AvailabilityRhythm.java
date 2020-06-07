package com.dvproject.vertTerm.Model;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum AvailabilityRhythm {
	/**
	 * on every day
	 */
	@JsonProperty("always")
	ALWAYS,
	/**
	 * only this one time
	 */
	@JsonProperty("oneTime")
	ONE_TIME,
	
	/**
	 * on every day
	 */
	@JsonProperty("daily")
	DAILY,
	
	/**
	 * on one day of a week
	 */
	@JsonProperty("weekly")
	WEEKLY,
	
	/**
	 * on one day of a month
	 */
	@JsonProperty("monthly")
	MONTHLY,
	
	/**
	 * on one day of a year
	 */
	@JsonProperty("yearly")
	YEARLY

}

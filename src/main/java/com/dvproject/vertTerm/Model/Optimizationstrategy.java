package com.dvproject.vertTerm.Model;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum Optimizationstrategy {
	/**
	 * optimization strategy to get the earliest end date
	 */
	@JsonProperty("earliestend")
	EARLIEST_END,
	
	/**
	 * optimization strategy to get the lowest amount of waiting time in between appointments
	 */
	@JsonProperty("lowestwaitingtime")
	LOWEST_WAITING_TIME,
	
	/**
	 * optimization strategy to get the least amount of distinct days for which appointments are planned
	 */
	@JsonProperty("leastnumberofdays")
	LEAST_NUMBER_OF_DAYS;
}

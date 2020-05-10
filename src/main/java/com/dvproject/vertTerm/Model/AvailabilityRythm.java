package com.dvproject.vertTerm.Model;

public enum AvailabilityRythm {
	/**
	 * only this one time
	 */
	ONE_TIME,
	
	/**
	 * on every day
	 */
	DAILY,
	
	/**
	 * on five days of a week (monday - friday)
	 */
	WORKINGWEEKLY,
	
	/**
	 * on one day of a week
	 */
	WEEKLY,
	
	/**
	 * on one day of a month
	 */
	MONTHLY,
	
	/**
	 * on one day of a year
	 */
	YEARLY;

}

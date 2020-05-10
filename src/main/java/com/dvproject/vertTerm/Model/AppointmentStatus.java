package com.dvproject.vertTerm.Model;

public enum AppointmentStatus {
	/**
	 * appointment has been completed, actual times has been set
	 */
	DONE,
	
	/**
	 * appointment target times have been set
	 */
	PLANNED,
	
	/**
	 * appointment has been created and can be booked
	 */
	CREATED,
	
	/**
	 * appointment has been cancelled and can only be booked again by an employee with the needed rights
	 */
	CANCELLED,
	
	/**
	 * appointment has been deactivated and can only be booked again by the customer himself
	 */
	DEACTIVATED,
	
	/**
	 * appointment has been deleted and can no longer be used
	 */
	DELETED;
}

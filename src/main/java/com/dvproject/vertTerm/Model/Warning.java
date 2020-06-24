package com.dvproject.vertTerm.Model;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum Warning {
	@JsonProperty("AppointmenttimeWarning")
	APPOINTMENT_TIME_WARNING,
	
	@JsonProperty("RestrictionWarning")
	RESTRICTION_WARNING,
	
	@JsonProperty("ProcedureRelationWarning")
	PROCEDURE_RELATION_WARNING,
	
	@JsonProperty("AvailabilityWarning")
	AVAILABILITY_WARNING,
	
	@JsonProperty("ResourceTypeWarning")
	RESOURCETYPE_WARNING,
	
	@JsonProperty("PositionWarning")
	POSITION_WARINING,
	
	@JsonProperty("ResourceWarning")
	RESOURCE_WARNING,
	
	@JsonProperty("EmployeeWarning")
	EMPLOYEE_WARNING,
	
	@JsonProperty("ProcedureWarning")
	PROCEDURE_WARNING,
	
	@JsonProperty("UserWarning")
	USER_WARNING,
	
	@JsonProperty("AppointmentWarning")
	APPOINTMENT_WARNING;
}

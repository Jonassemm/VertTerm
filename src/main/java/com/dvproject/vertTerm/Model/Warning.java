package com.dvproject.vertTerm.Model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum Warning {
	@JsonProperty("AppointmenttimeWarning")
	APPOINTMENT_TIME_WARNING("AppointmenttimeWarning"),

	@JsonProperty("RestrictionWarning")
	RESTRICTION_WARNING("RestrictionWarning"),

	@JsonProperty("ProcedureRelationWarning")
	PROCEDURE_RELATION_WARNING("ProcedureRelationWarning"),

	@JsonProperty("AvailabilityWarning")
	AVAILABILITY_WARNING("AvailabilityWarning"),

	@JsonProperty("ResourceTypeWarning")
	RESOURCETYPE_WARNING("ResourceTypeWarning"),

	@JsonProperty("PositionWarning")
	POSITION_WARINING("PositionWarning"),

	@JsonProperty("ResourceWarning")
	RESOURCE_WARNING("ResourceWarning"),

	@JsonProperty("EmployeeWarning")
	EMPLOYEE_WARNING("EmployeeWarning"),

	@JsonProperty("ProcedureWarning")
	PROCEDURE_WARNING("ProcedureWarning"),

	@JsonProperty("UserWarning")
	USER_WARNING("UserWarning"),

	@JsonProperty("AppointmentWarning")
	APPOINTMENT_WARNING("AppointmentWarning");

	private String name;

	private Warning (String warningString) {
		name = warningString;
	}

	private static Map<String, Warning> lookup = new HashMap<>();

	static {
		for (Warning warning : Warning.values()) {
			lookup.put(warning.name, warning);
		}
	}

	public static Warning enumOf(String value) {
		return lookup.get(value);
	}
	
	public static List<Warning> enumOf(List<String> values) {
		return values.stream().map(value -> enumOf(value)).collect(Collectors.toList());
	}
	
	public static List<Warning> getAll() {
		return new ArrayList<>(lookup.values());
	}

}

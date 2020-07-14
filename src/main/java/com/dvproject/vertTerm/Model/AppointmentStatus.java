package com.dvproject.vertTerm.Model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum AppointmentStatus {
	/**
	 * appointment has been completed, actual times have been set
	 */
	@JsonProperty("done")
	DONE ("done"),
	
	/**
	 * appointment planned times have been set
	 */
	@JsonProperty("planned")
	PLANNED ("planned"),

	/**
	 * appointment optimized
	 */
	@JsonProperty("recommended")
	RECOMMENDED("recommended"),

	/**
	 * appointment open for optimization
	 */
	@JsonProperty("open")
	OPEN("open"),
	/**
	 * appointment has been deleted and can no longer be used
	 */
	@JsonProperty("deleted")
	DELETED ("deleted");

	private String name;

	private AppointmentStatus (String name) {
		this.name = name;
	}

	private static Map<String, AppointmentStatus> lookup = new HashMap<>();

	static {
		for (AppointmentStatus appointmentStatus : AppointmentStatus.values()) {
			lookup.put(appointmentStatus.name, appointmentStatus);
		}
	}

	public static AppointmentStatus enumOf(String value) {
		return lookup.get(value);
	}

	public static List<AppointmentStatus> enumOf(List<String> values) {
		return values.stream().map(value -> enumOf(value)).collect(Collectors.toList());
	}

	public static List<AppointmentStatus> getAll() {
		return new ArrayList<>(lookup.values());
	}
	
	public boolean isDeleted () {
		return this == DELETED;
	}
	
	public boolean isDone () {
		return this == DONE;
	}
}

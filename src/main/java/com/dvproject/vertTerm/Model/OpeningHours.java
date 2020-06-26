package com.dvproject.vertTerm.Model;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;

public class OpeningHours{
	@Id
	private String id;
	@DBRef
	private List<Availability> availabilities;
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public List<Availability> getAvailabilities() {
		return availabilities;
	}
	
	public void setAvailabilities(List<Availability> availabilities) {
		this.availabilities = availabilities;
	}
}

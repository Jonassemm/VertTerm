package com.dvproject.vertTerm.Model;

import java.io.Serializable;
import java.util.List;

import javax.validation.constraints.NotEmpty;

import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("user")
public class Employee extends User implements Serializable
{
    private static final long serialVersionUID = -4432631544443788288L;
    
	@NotEmpty
	private List<Availability> availabilities;
	@DBRef
	private Employee supervisor;

	public List<Availability> getAvailabilities() {
		return availabilities;
	}

	public void setAvailabilities(List<Availability> availabilities) {
		this.availabilities = availabilities;
	}

}

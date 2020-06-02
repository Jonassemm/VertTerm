package com.dvproject.vertTerm.Model;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("user")
public class Employee extends User implements Serializable
{
    private static final long serialVersionUID = -4432631544443788288L;

	private List<Availability> availabilities;
	@DBRef
	private List<Position> positions;

	public List<Availability> getAvailabilities() {
		return availabilities;
	}

	public void setAvailabilities(List<Availability> availabilities) {
		this.availabilities = availabilities;
	}

	public List<Position> getPositions() {
		return positions;
	}

	public void setPositions(List<Position> positions) {
		this.positions = positions;
	}

}

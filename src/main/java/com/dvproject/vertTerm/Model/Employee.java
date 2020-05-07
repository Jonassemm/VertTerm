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
    
    private boolean isAvailable;
    
    @DBRef
    private Position position;
	@NotEmpty
	private List<Availability> availabilities;
	@DBRef
	private Employee supervisor;
    
    public boolean isAvailable() {
        return isAvailable;
    }
    
    public void setAvailable(boolean isAvailable) {
        this.isAvailable = isAvailable;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

	public List<Availability> getAvailabilities() {
		return availabilities;
	}

	public void setAvailabilities(List<Availability> availabilities) {
		this.availabilities = availabilities;
	}

}

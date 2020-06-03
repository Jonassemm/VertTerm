package com.dvproject.vertTerm.Model;

import java.io.Serializable;
import java.time.Duration;
import java.util.Date;
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

	private Date getAvailableDateByAvailablility(Date date, Duration duration){
		Date earliestDate = null;
		for(Availability availability : this.getAvailabilities()){
			Date currentBestAvailability = availability.getEarliestAvailability(date, duration);
			if(currentBestAvailability != null){
				if(earliestDate == null){
					earliestDate = currentBestAvailability;
				}
				else if(earliestDate.before(earliestDate)){
					earliestDate = currentBestAvailability;
				}
			}
		}
		return earliestDate;
	}

	@Override
	public Date getAvailableDate(Date date, Duration duration) {
		Date dateByAvailability = this.getAvailableDateByAvailablility(date, duration);
		Date dateByAppointment = this.getAvailableDateByAppointments(date, duration);
		if(dateByAvailability == null){
			return null;
		}
		if(dateByAvailability.after(date)){
			return this.getAvailableDate(dateByAvailability, duration);
		}
		if(dateByAppointment.after(date)){
			return this.getAvailableDate(dateByAppointment, duration);
		}
		return date;
	}
}

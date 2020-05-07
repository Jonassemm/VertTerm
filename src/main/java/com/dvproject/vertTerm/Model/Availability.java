package com.dvproject.vertTerm.Model;

import java.util.Date;

public class Availability {	
	private boolean isAvailable;
	private Date startOfAvailibleTime;
	private Date endOfAvailableTimes;
	
	/**
	 * defines the type of rythm (e.g. daily or weekly)
	 */
	private AvailabilityRythm availabilityRythm;
	
	/**
	 * defines the frequenzy of the rythm (e.g. 2 -> every two rythms)
	 */
	private int frequenzy;
	private Date endOfAvailability;
	
	public boolean isAvailable() {
		return isAvailable;
	}
	
	public void setAvailable(boolean isAvailable) {
		this.isAvailable = isAvailable;
	}
	
	public Date getStartOfAvailibleTime() {
		return startOfAvailibleTime;
	}
	
	public void setStartOfAvailibleTime(Date startOfAvailibleTime) {
		this.startOfAvailibleTime = startOfAvailibleTime;
	}

	public Date getEndOfAvailableTimes() {
		return endOfAvailableTimes;
	}

	public void setEndOfAvailableTimes(Date endOfAvailableTimes) {
		this.endOfAvailableTimes = endOfAvailableTimes;
	}

	public AvailabilityRythm getAvailabilityRythm() {
		return availabilityRythm;
	}

	public void setAvailabilityRythm(AvailabilityRythm availabilityRythm) {
		this.availabilityRythm = availabilityRythm;
	}

	public int getFrequenzy() {
		return frequenzy;
	}

	public void setFrequenzy(int frequenzy) {
		this.frequenzy = frequenzy;
	}

	public Date getEndOfAvailability() {
		return endOfAvailability;
	}

	public void setEndOfAvailability(Date endOfAvailability) {
		this.endOfAvailability = endOfAvailability;
	}
	
}

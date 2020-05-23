package com.dvproject.vertTerm.Model;

import java.util.Date;

import org.springframework.data.annotation.PersistenceConstructor;

public class Availability {	
	private Date startDate;
	private Date endDate;
	
	/**
	 * defines the type of rhythm (e.g. daily or weekly)
	 */
	private AvailabilityRhythm rhythm;
	
	/**
	 * defines the frequency of the rhythm (e.g. 2 -> every two rhythms)
	 */
	private int frequency;
	private Date endOfSeries;
	
	public Availability (Date startDate, Date endDate, AvailabilityRhythm rythm) {
		this(startDate, endDate, rythm, 1);
	}
	
	public Availability (Date startDate, Date endDate, AvailabilityRhythm rhythm, int frequency) {
		this.startDate = startDate;
		this.endDate = endDate;
		this.rhythm = rhythm;
		this.frequency = frequency;
	}
	
	@PersistenceConstructor
	public Availability (Date startDate, Date endDate, AvailabilityRhythm rhythm, Date endOfSeries, int frequency) {
		this.startDate = startDate;
		this.endDate = endDate;
		this.endOfSeries = endOfSeries;
		this.rhythm = rhythm;
		this.frequency = frequency;
	}
	
	public Availability () {
	}
	
	public Date getStartDate() {
		return startDate;
	}

	public Date getEndDate() {
		return endDate;
	}
	
	public AvailabilityRhythm getRhythm() {
		return rhythm;
	}
	
	public int getFrequency() {
		return frequency;
	}
	
	public Date getEndOfSeries() {
		return endOfSeries;
	}
	
	public void setEndOfSeries(Date endOfSeries) {
		this.endOfSeries = endOfSeries;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public void setRhythm(AvailabilityRhythm rhythm) {
		this.rhythm = rhythm;
	}

	public void setFrequency(int frequency) {
		this.frequency = frequency;
	}
	
}

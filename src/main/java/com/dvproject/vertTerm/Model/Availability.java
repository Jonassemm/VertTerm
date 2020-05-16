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
	 * defines the frequenzy of the rhythm (e.g. 2 -> every two rhythms)
	 */
	private int frequenzy;
	private Date endOfSeries;
	
	public Availability (Date startDate, Date endDate, AvailabilityRhythm rythm) {
		this(startDate, endDate, rythm, 1);
	}
	
	public Availability (Date startDate, Date endDate, AvailabilityRhythm rhythm, int frequenzy) {
		this.startDate = startDate;
		this.endDate = endDate;
		this.rhythm = rhythm;
		this.frequenzy = frequenzy;
	}
	
	@PersistenceConstructor
	public Availability (Date startDate, Date endDate, AvailabilityRhythm rhythm, Date endOfSeries, int frequenzy) {
		this.startDate = startDate;
		this.endDate = endDate;
		this.endOfSeries = endOfSeries;
		this.rhythm = rhythm;
		this.frequenzy = frequenzy;
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
	
	public int getFrequenzy() {
		return frequenzy;
	}
	
	public Date getEndOfSeries() {
		return endOfSeries;
	}
	
	public void setEndOfSeries(Date endOfSeries) {
		this.endOfSeries = endOfSeries;
	}
	
}

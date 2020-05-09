package com.dvproject.vertTerm.Model;

import java.util.Date;

public class Availability {	
	private Date startDate;
	private Date endDate;
	
	/**
	 * defines the type of rythm (e.g. daily or weekly)
	 */
	private AvailabilityRythm rythm;
	
	/**
	 * defines the frequenzy of the rythm (e.g. 2 -> every two rythms)
	 */
	private int frequenzy;
	private Date endOfSeries;
	
	public Availability (Date startDate, Date endDate, AvailabilityRythm rythm) {
		this(startDate, endDate, rythm, 1);
	}
	
	public Availability (Date startDate, Date endDate, AvailabilityRythm rythm, int frequenzy) {
		this.startDate = startDate;
		this.endDate = endDate;
		this.rythm = rythm;
		this.frequenzy = frequenzy;
	}
	
	public Date getStartDate() {
		return startDate;
	}

	public Date getEndDate() {
		return endDate;
	}
	
	public AvailabilityRythm getRythm() {
		return rythm;
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

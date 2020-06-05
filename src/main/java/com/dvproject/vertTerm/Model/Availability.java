package com.dvproject.vertTerm.Model;

import java.time.Duration;
import java.util.Calendar;
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

	public Date getEarliestAvailability(Date date, Duration duration){
		if((endDate.getTime() - startDate.getTime()) < duration.toMillis()){
			return null;
		}

		Date end = new Date(date.getTime() + duration.toMillis());
		if(this.getRhythm() == AvailabilityRhythm.ONE_TIME){
			return end.after(endDate) ? null : date;
		}
		if(end.after(endOfSeries)){
			return null;
		}

		Date tmpStartDate = startDate;
		Date tmpEndDate = endDate;

		while(!tmpStartDate.after(endOfSeries)){
			if(tmpEndDate.before(end)){
				Calendar startCalendar = Calendar.getInstance();
				Calendar endCalendar = Calendar.getInstance();

				startCalendar.setTime(tmpStartDate);
				endCalendar.setTime(tmpEndDate);

				switch (this.getRhythm()){
					case DAILY:
						startCalendar.add(Calendar.HOUR, 24 * this.getFrequency());
						endCalendar.add(Calendar.HOUR, 24 * this.getFrequency());
						break;
					case WEEKLY:
						startCalendar.add(Calendar.HOUR, 7 * 24 * this.getFrequency());
						endCalendar.add(Calendar.HOUR, 7 * 24 * this.getFrequency());
						break;
					case MONTHLY:
						startCalendar.add(Calendar.MONTH, this.getFrequency());
						endCalendar.add(Calendar.MONTH, this.getFrequency());
						break;
					case YEARLY:
						startCalendar.add(Calendar.YEAR, this.getFrequency());
						endCalendar.add(Calendar.YEAR, this.getFrequency());
						break;
				}
				tmpStartDate = startCalendar.getTime();
				tmpEndDate = endCalendar.getTime();
			}
			else{
				if(date.before(tmpStartDate)){
					return tmpStartDate;
				}
				else{
					return date;
				}
			}
		}
		return null;
	}
	
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

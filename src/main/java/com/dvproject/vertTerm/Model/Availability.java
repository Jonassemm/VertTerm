package com.dvproject.vertTerm.Model;

import java.time.Duration;
import java.time.YearMonth;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

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
	
	public boolean isAvailableBetween (Date startdate, Date enddate) {
		if (startDate.after(startdate)
				|| (endOfSeries != null && enddate.after(endOfSeries))) {
			return false;
		}
		
		boolean retVal = true;
		int availStartDays = 0, availEndDays = 0, startDays = 0, endDays = 0, difference = 0;
		Calendar availStartCalendar = getCalendar(this.startDate);
		Calendar availEndCalendar = getCalendar(this.endDate);
		Calendar startCal = getCalendar(startdate);
		Calendar endCal = getCalendar(enddate);

		switch (rhythm) {
		case DAILY:
			difference = getDaysBetweenDates(startCal, availStartCalendar);
			break;
		case WEEKLY:
			int availStartDaysMod = availStartCalendar.get(Calendar.DAY_OF_WEEK);
			int availEndDaysMod = availEndCalendar.get(Calendar.DAY_OF_WEEK);
			int startDaysMod = startCal.get(Calendar.DAY_OF_WEEK);
			int endDaysMod = endCal.get(Calendar.DAY_OF_WEEK);

			availEndDays = normalize(availEndDaysMod, availStartDaysMod, 7);
			startDays = normalize(startDaysMod, availStartDaysMod, 7);
			endDays = normalize(endDaysMod, availStartDaysMod, 7);
			difference = getDaysBetweenDates(availStartCalendar, startCal);

			retVal = getDaysBetweenDates(startCal, endCal) < 7;

			break;
		case MONTHLY:
			int daysInMonthOfAvailabilityStartdate = YearMonth
					.of(availStartCalendar.get(Calendar.YEAR), availStartCalendar.get(Calendar.MONTH) + 1).lengthOfMonth();

			int availStartDaysOfMonth = availStartCalendar.get(Calendar.DAY_OF_MONTH);
			int availEndDaysOfMonth = availEndCalendar.get(Calendar.DAY_OF_MONTH);
			int startDaysOfMonth = startCal.get(Calendar.DAY_OF_MONTH);
			int endDaysOfMonth = endCal.get(Calendar.DAY_OF_MONTH);

			availEndDays = normalize(availEndDaysOfMonth, availStartDaysOfMonth, daysInMonthOfAvailabilityStartdate);
			startDays = normalize(startDaysOfMonth, availStartDaysOfMonth, daysInMonthOfAvailabilityStartdate);
			endDays = normalize(endDaysOfMonth, availStartDaysOfMonth, daysInMonthOfAvailabilityStartdate);
			difference = startCal.get(Calendar.MONTH) - availStartCalendar.get(Calendar.MONTH)
					+ getYearDifference(startCal, availStartCalendar) * 12;

			retVal = getDaysBetweenDates(startCal, endCal) < 7;

			break;
		case YEARLY:
			int daysInYearOfAvailabilityStartdate = getAmoungOfDaysInYear(availStartCalendar);

			int availStartDaysOfYear = availStartCalendar.get(Calendar.DAY_OF_YEAR);
			int availEndDaysOfYear = availEndCalendar.get(Calendar.DAY_OF_YEAR);
			int startDaysOfYear = startCal.get(Calendar.DAY_OF_YEAR);
			int endDaysOfYear = endCal.get(Calendar.DAY_OF_YEAR);

			availEndDays = normalize(availEndDaysOfYear, availStartDaysOfYear, daysInYearOfAvailabilityStartdate);
			startDays = normalize(startDaysOfYear, availStartDaysOfYear, daysInYearOfAvailabilityStartdate);
			endDays = normalize(endDaysOfYear, availStartDaysOfYear, daysInYearOfAvailabilityStartdate);
			difference = startCal.get(Calendar.YEAR) - availStartCalendar.get(Calendar.YEAR);

			retVal = getDaysBetweenDates(startCal, endCal) < 366;

			break;
		default:
			retVal = false;
		}

		return retVal && this.isInBetween(availStartCalendar, availEndCalendar, startCal, endCal, frequency, availStartDays,
				availEndDays, startDays, endDays, difference);
	}
	
	private boolean isInBetween(Calendar availStartdate, Calendar availEnddate, Calendar startdate, Calendar enddate,
			int frequenzy, int availStartDays, int availEndDays, int startDays, int endDays, int frequenzyDiff) {
		if (availStartDays <= startDays && startDays <= availEndDays && endDays <= availEndDays) {
			boolean retVal = true;
			if (availStartDays == startDays) {
				retVal = retVal && date1IsAfterDate2(availStartdate, startdate, Calendar.HOUR_OF_DAY)
						&& date1IsAfterDate2(availStartdate, startdate, Calendar.MINUTE);
			}
			if (availEndDays == endDays) {
				retVal = retVal && date1IsAfterDate2(enddate, availEnddate, Calendar.HOUR_OF_DAY)
						&& date1IsAfterDate2(enddate, availEnddate, Calendar.MINUTE);
			}
			return retVal && (frequenzyDiff % frequenzy == 0);
		}

		return false;
	}

	private int getAmoungOfDaysInYear(Calendar cal) {
		return cal.getActualMaximum(Calendar.DAY_OF_YEAR);
	}

	private int normalize(int dateToNormalize, int dateNormalizer, int additionValue) {
		dateToNormalize -= dateNormalizer;
		return dateToNormalize + (dateToNormalize < 0 ? additionValue : 0);
	}

	private boolean date1IsAfterDate2(Calendar date1, Calendar date2, int testCode) {
		return date1.get(testCode) <= date2.get(testCode);
	}

	private int getDaysBetweenDates(Calendar date1, Calendar date2) {
		return getTimeBetweenDates(date1, date2, 1000 * 60 * 60 * 24);
	}

	private int getTimeBetweenDates(Calendar date1, Calendar date2, int Conversionrate) {
		return (int) ((date1.getTimeInMillis() - date2.getTimeInMillis()) / Conversionrate);
	}

	private int getYearDifference(Calendar date1, Calendar date2) {
		int diff = date1.get(Calendar.YEAR) - date2.get(Calendar.YEAR);

		return diff < 0 ? 0 : diff;
	}

	private Calendar getCalendar(Date date) {
		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		calendar.setTime(date);
		return calendar;
	}
}

package com.dvproject.vertTerm.Service;

import java.time.YearMonth;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.springframework.stereotype.Service;

import com.dvproject.vertTerm.Model.Availability;
import com.dvproject.vertTerm.Model.AvailabilityRhythm;

@Service
public class AvailabilityServiceImpl {
	private static final int ConvertionrateFromMilisToDay = 1000 * 60 * 60 * 24;

	/**
	 * 
	 * @param availabilities All the availabilities of the object that need to be
	 *                       considered
	 * @param startdate      The start date from which the availabilities should be
	 *                       tested
	 * @param enddate        The end date to which the availabilities should be
	 *                       tested
	 * @return A boolean that depicts whether or not the object is available in the
	 *         given time interval
	 */
	public boolean isAvailable(List<Availability> availabilities, Date startdate, Date enddate) {
		boolean isAvailable;

		if (!enddate.after(startdate)) {
			throw new IllegalArgumentException("The startdate must be before the enddate!");
		}

		for (Availability availability : availabilities) {
			// tests, whether the given dates are in the interval of the availability
			if (!(availability.getStartDate().after(startdate)
					|| (availability.getEndOfSeries() != null && enddate.after(availability.getEndOfSeries())))) {

				// transforms the dates into calendar Objects and calls the respective method to
				// process this data
				isAvailable = isInBetween(getCalendar(startdate), getCalendar(enddate),
						getCalendar(availability.getStartDate()), getCalendar(availability.getEndDate()),
						availability.getRhythm(), availability.getFrequency());

				// if an availability is found for the given time interval, return true
				if (isAvailable)
					return true;
			}
		}
		// no availablity for the given time interval has been found
		return false;
	}

	private boolean isInBetween(Calendar startdate, Calendar enddate, Calendar availStartdate, Calendar availEnddate,
			AvailabilityRhythm rhythm, int frequenzy) {
		boolean retVal = true;
		int availStartDays = 0, availEndDays = 0, startDays = 0, endDays = 0, difference = 0;

		switch (rhythm) {
		case DAILY:
			difference = getDaysBetween(startdate, availStartdate);
			break;
		case WEEKLY:
			int availStartDaysMod = getDaysFromEpoch(availStartdate) % 7;
			int availEndDaysMod = getDaysFromEpoch(availEnddate) % 7;
			int startDaysMod = getDaysFromEpoch(startdate) % 7;
			int endDaysMod = getDaysFromEpoch(enddate) % 7;

			int availStartDaysDif = getDaysFromEpoch(availStartdate) / 7;
			int startDaysDif = getDaysFromEpoch(startdate) / 7;

			availEndDays = normalize(availEndDaysMod, availStartDaysMod, 7);
			startDays = normalize(startDaysMod, availStartDaysMod, 7);
			endDays = normalize(endDaysMod, availStartDaysMod, 7);
			difference = startDaysDif - availStartDaysDif;

			retVal = getDaysBetween(startdate, enddate) < 7;

			break;
		case MONTHLY:
			int daysInMonthOfAvailabilityStartdate = YearMonth
					.of(availStartdate.get(Calendar.YEAR), availStartdate.get(Calendar.MONTH) + 1).lengthOfMonth();

			int availStartDaysOfMonth = availStartdate.get(Calendar.DAY_OF_MONTH);
			int availEndDaysOfMonth = availEnddate.get(Calendar.DAY_OF_MONTH);
			int startDaysOfMonth = startdate.get(Calendar.DAY_OF_MONTH);
			int endDaysOfMonth = enddate.get(Calendar.DAY_OF_MONTH);

			availEndDays = normalize(availEndDaysOfMonth, availStartDaysOfMonth, daysInMonthOfAvailabilityStartdate);
			startDays = normalize(startDaysOfMonth, availStartDaysOfMonth, daysInMonthOfAvailabilityStartdate);
			endDays = normalize(endDaysOfMonth, availStartDaysOfMonth, daysInMonthOfAvailabilityStartdate);
			difference = startdate.get(Calendar.MONTH) - availStartdate.get(Calendar.MONTH)
					+ getYearDifference(startdate, availStartdate) * 12;

			retVal = getDaysBetween(startdate, enddate) < 7;

			break;
		case YEARLY:
			int daysInYearOfAvailabilityStartdate = getAmoungOfDaysInYear(availStartdate);

			int availStartDaysOfYear = availStartdate.get(Calendar.DAY_OF_YEAR);
			int availEndDaysOfYear = availEnddate.get(Calendar.DAY_OF_YEAR);
			int startDaysOfYear = startdate.get(Calendar.DAY_OF_YEAR);
			int endDaysOfYear = enddate.get(Calendar.DAY_OF_YEAR);

			availEndDays = normalize(availEndDaysOfYear, availStartDaysOfYear, daysInYearOfAvailabilityStartdate);
			startDays = normalize(startDaysOfYear, availStartDaysOfYear, daysInYearOfAvailabilityStartdate);
			endDays = normalize(endDaysOfYear, availStartDaysOfYear, daysInYearOfAvailabilityStartdate);
			difference = startdate.get(Calendar.YEAR) - availStartdate.get(Calendar.YEAR);

			retVal = getDaysBetween(startdate, enddate) < 366;

			break;
		default:
			retVal = false;
		}

		return retVal && this.isInBetween(availStartdate, availEnddate, startdate, enddate, frequenzy, availStartDays,
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
	
	private int getAmoungOfDaysInYear (Calendar cal) {
		return cal.getActualMaximum(Calendar.DAY_OF_YEAR);
	}

	private int normalize(int dateToNormalize, int dateNormalizer, int additionValue) {
		dateToNormalize -= dateNormalizer;
		return dateToNormalize + (dateToNormalize < 0 ? additionValue : 0);
	}

	private boolean date1IsAfterDate2(Calendar date1, Calendar date2, int testCode) {
		return date1.get(testCode) <= date2.get(testCode);
	}

	private int getDaysBetween(Calendar date1, Calendar date2) {
		return getTimeBetween(date1, date2, ConvertionrateFromMilisToDay);
	}

	private int getTimeBetween(Calendar date1, Calendar date2, int Conversionrate) {
		return (int) ((date1.getTimeInMillis() - date2.getTimeInMillis()) / Conversionrate);
	}

	private int getDaysFromEpoch(Calendar date) {
		return getTimeFromEpoch(date, ConvertionrateFromMilisToDay);
	}

	private int getTimeFromEpoch(Calendar date, int Conversionrate) {
		return (int) (date.getTimeInMillis() / Conversionrate);
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

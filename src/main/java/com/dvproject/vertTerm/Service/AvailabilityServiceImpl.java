package com.dvproject.vertTerm.Service;

import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;

import com.dvproject.vertTerm.Model.Availability;

@Service
public class AvailabilityServiceImpl {
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
		boolean isAvailable = false;

		if (!enddate.after(startdate)) {
			throw new IllegalArgumentException("The startdate must be before the enddate!");
		}

		for (Availability availability : availabilities) {
			/*
			 * transforms the dates into calendar Objects and calls the respective method to
			 * process this data
			 */
			isAvailable = availability.isAvailableBetween(startdate, enddate);

			/*
			 * if an availability is found for the given time interval jump out of the loop
			 * and return true
			 */
			if (isAvailable)
				break;
		}

		return isAvailable;
	}

}

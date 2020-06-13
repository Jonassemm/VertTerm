package com.dvproject.vertTerm.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dvproject.vertTerm.Model.Appointment;
import com.dvproject.vertTerm.Model.Availability;
import com.dvproject.vertTerm.Model.Available;
import com.dvproject.vertTerm.Model.BookingTester;
import com.dvproject.vertTerm.Model.OverrideBookingTester;
import com.dvproject.vertTerm.repository.AvailabilityRepository;

@Service
public class AvailabilityServiceImpl {

	@Autowired
	private AppointmentService appointmentService;

	@Autowired
	private AvailabilityRepository availRepo;

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

	public void update(List<Availability> availabilities, Available entity) {
		Availability repoAvailability;
		boolean endetAvailability = false;
		Date earliestEndOfSeries = null;

		for (int i = 0; i < availabilities.size(); i++) {
			Availability availability = availabilities.get(i);

			if (availability.getId() != null) {
				repoAvailability = availRepo.findById(availability.getId()).orElse(null);
				if (repoAvailability != null) {
					if (repoAvailability.getEndOfSeries() == null && availability.getEndOfSeries() != null) {
						Date endOfSeries = availability.getEndOfSeries();
						repoAvailability.setEndOfSeries(endOfSeries);
						availRepo.save(repoAvailability);

						endetAvailability = true;
						if (earliestEndOfSeries == null || endOfSeries.before(earliestEndOfSeries)) {
							earliestEndOfSeries = endOfSeries;
						}
					}
				} else {
					repoAvailability = create(availability);
				}
			} else {
				repoAvailability = create(availability);
			}

			availability = repoAvailability;
		}

		if (endetAvailability) {
			BookingTester tester = new OverrideBookingTester();
			List<Appointment> appointmentsToTest = entity.getAppointmentsOfAvailable(appointmentService,
					earliestEndOfSeries);

			for (Appointment appointment : appointmentsToTest) {
				tester.setAppointment(appointment);

				tester.testAvailabilities();

				appointmentService.update(appointment);
			}
		}
	}

	public Availability create(Availability availability) {
		if (availability.getEndDate().before(availability.getStartDate()) && availability.getEndOfSeries() == null) {
			throw new IllegalArgumentException("Enddate is before startdate");
		}

		return availRepo.save(availability);
	}

	public void loadAllAvailabilitiesOfEntity(List<Availability> availabilities, Available entity,
			AvailabilityService availabilityService) {
		update(availabilities, entity);

		// id -> Availability
		Map<String, Availability> availabilitiesMap = availabilities.stream()
				.collect(Collectors.toMap(Availability::getId, availability -> availability));
		List<Availability> availabilitiesFromDB = availabilityService.getAllAvailabilities(entity.getId());

		// Load all availabilities from db
		for (int i = 0; i < availabilitiesFromDB.size(); i++) {
			Availability availabilityFromDB = availabilitiesFromDB.get(i);

			if (!availabilitiesMap.containsKey(availabilityFromDB.getId())) {
				availabilities.add(availabilityFromDB);
			}
		}
	}

	public void loadAllAvailablitiesOfEntityViaId(List<Availability> availabilities, String id,
			AvailabilityService availabilityService) {
		loadAllAvailabilitiesOfEntity(availabilities, availabilityService.getById(id), availabilityService);
	}
}

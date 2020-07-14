package com.dvproject.vertTerm.Service;

import java.util.*;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dvproject.vertTerm.Model.*;
import com.dvproject.vertTerm.exception.AvailabilityException;
import com.dvproject.vertTerm.repository.AvailabilityRepository;
import com.dvproject.vertTerm.util.AppointmentTester;
import com.dvproject.vertTerm.util.NormalAppointmentTester;

/**
 * @author Joshua Müller
 */
@Service
public class AvailabilityServiceImpl {

	@Autowired
	private AppointmentService appointmentService;

	@Autowired
	private HttpServletResponse response;

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
		boolean availabilityHasChanged = false;
		Date earliestDateChanged = null;

		// test, whether all availabilities.id are unique
		if (hasNotUniqueIds(availabilities))
			throw new IllegalArgumentException("Non unique ids in the given availabilities");

		for (int i = 0; i < availabilities.size(); i++) {
			Availability availability = availabilities.get(i);

			if (availability.getId() != null) {
				repoAvailability = availRepo.findById(availability.getId()).orElse(null);
				if (repoAvailability != null) {
					// endOfSeries should be set
					if (repoAvailability.getEndOfSeries() == null && availability.getEndOfSeries() != null) {
						Date endOfSeries = availability.getEndOfSeries();
						repoAvailability.setEndOfSeries(endOfSeries);
						availRepo.save(repoAvailability);

						if (!availabilityHasChanged || endOfSeries.before(earliestDateChanged))
							earliestDateChanged = endOfSeries;
					}
				} else { // id != null && no entity in db
					repoAvailability = create(availability);

					Date startdate = availability.getStartDate();
					if (!availabilityHasChanged || startdate.before(earliestDateChanged))
						earliestDateChanged = startdate;
				}
			} else { // id == null
				repoAvailability = create(availability);

				Date startdate = availability.getStartDate();
				if (!availabilityHasChanged || startdate.before(earliestDateChanged))
					earliestDateChanged = startdate;
			}

			availabilityHasChanged = earliestDateChanged != null;
			availabilities.set(i, repoAvailability);
		}

		if (availabilityHasChanged && entity.getId() != null)
			testAppointmentsOfAvailable(entity, earliestDateChanged);
	}

	public Availability create(Availability availability) {
		if (availability.getEndDate().before(availability.getStartDate()) && availability.getEndOfSeries() == null)
			throw new IllegalArgumentException("Enddate is before startdate");

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
		availabilities.addAll(availabilitiesFromDB.stream().filter(avail -> !availabilitiesMap.containsKey(avail.getId()))
				.collect(Collectors.toList()));
	}

	public void loadAllAvailablitiesOfEntityViaId(List<Availability> availabilities, String id,
			AvailabilityService availabilityService) {
		loadAllAvailabilitiesOfEntity(availabilities, availabilityService.getById(id), availabilityService);
	}

	private void testAppointmentsOfAvailable(Available entity, Date startdateOfTest) {
		AppointmentTester bookingTester = new NormalAppointmentTester();
		List<Appointment> appointmentsToTest = entity.getAppointmentsAfterDate(appointmentService, startdateOfTest);
		boolean hasNewWarning = false;

		for (Appointment appointment : appointmentsToTest) {
			Date startdate = appointment.getPlannedStarttime();
			Date enddate = appointment.getPlannedEndtime();
			List<Warning> warningsOfAppointment = appointment.getWarnings();
			boolean hasChanged = false;

			try {
				if (warningsOfAppointment.contains(Warning.AVAILABILITY_WARNING)) {
					bookingTester.setAppointment(appointment);
					bookingTester.testAvailabilities();
					appointment.removeWarning(Warning.AVAILABILITY_WARNING);
					hasChanged = true;
				} else {
					entity.isAvailable(startdate, enddate);
				}
			} catch (AvailabilityException ex) {
				hasChanged     = appointment.addWarnings(Warning.AVAILABILITY_WARNING);
				hasNewWarning |= hasChanged;
			} finally {
				if (hasChanged)
					appointmentService.update(appointment);
			}
		}

		if (hasNewWarning)
			response.addHeader("exception", Availability.class.getSimpleName());
	}

	private boolean hasNotUniqueIds(List<Availability> availabilities) {
		List<String> notNullIds = availabilities.stream()
									.filter(avail -> avail.getId() != null)
									.map(avail -> avail.getId())
									.collect(Collectors.toList());
		
		int nonNullIdsCount = notNullIds.size();
		int uniqueNotNullIdsCount = (int) notNullIds.stream().distinct().count();
		
		return uniqueNotNullIdsCount != nonNullIdsCount;
	}
}

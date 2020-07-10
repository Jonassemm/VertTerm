package com.dvproject.vertTerm.Service;

import java.util.*;
import java.util.stream.Collectors;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.dvproject.vertTerm.Model.Availability;
import com.dvproject.vertTerm.Model.OpeningHours;
import com.dvproject.vertTerm.repository.OpeningHoursRepository;

/**
 * @author Joshua Müller
 */
@Service
public class OpeningHoursServiceImp implements OpeningHoursService {
	@Autowired
	private OpeningHoursRepository openingHoursRepository;

	@Override
	@PreAuthorize("hasAuthority('OPENING_HOURS_READ')")
	public OpeningHours get() {
		List<OpeningHours> openingHours = openingHoursRepository.findAll();

		if (openingHours == null || openingHours.size() == 0)
			throw new RuntimeException("Two OpeningHourshave not been setup correctly, please restart the webapp");

		if (openingHours.size() > 1)
			throw new RuntimeException("Two OpeningHours objects in database!");

		return openingHoursRepository.findAll().get(0);
	}

	@Override
	@PreAuthorize("hasAuthority('OPENING_HOURS_WRITE')")
	public OpeningHours update(OpeningHours updatedInstance) {
		List<Availability> availabilitiesToInsert = new ArrayList<>();
		Map<String, Availability> updatedAvailabilities = new HashMap<>();
		OpeningHours openingHours = get();
		List<Availability> openingHoursAvailabilities = openingHours.getAvailabilities();

		for (Availability availability : updatedInstance.getAvailabilities()) {
			if (availability.getId() == null) {
				availability.setId(new ObjectId ().toString());
				availabilitiesToInsert.add(availability);
			} else
				updatedAvailabilities.put(availability.getId(), availability);
		}

		for (Availability availability : openingHoursAvailabilities) {
			Availability updatedAvailability = updatedAvailabilities.get(availability.getId());
			if (updatedAvailability != null && availability.getEndOfSeries() == null
					&& updatedAvailability.getEndOfSeries() != null)
				availability.setEndOfSeries(updatedAvailability.getEndOfSeries());
		}
		
		openingHoursAvailabilities.addAll(availabilitiesToInsert);
		
		openingHoursRepository.save(openingHours);
		
		return get();
	}

	@Override
	public void deleteAvailabilitiesInThePast() {
		OpeningHours openingHours = get();
		List<Availability> openingHoursAvailabilities = openingHours.getAvailabilities();
		Date now = new Date();

		openingHours.setAvailabilities(openingHoursAvailabilities.stream()
				.filter(avail -> avail.getEndOfSeries() == null || avail.getEndOfSeries().after(now))
				.collect(Collectors.toList()));

		openingHoursRepository.save(openingHours);
	}

}

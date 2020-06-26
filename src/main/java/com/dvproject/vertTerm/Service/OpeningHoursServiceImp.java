package com.dvproject.vertTerm.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dvproject.vertTerm.Model.Availability;
import com.dvproject.vertTerm.Model.OpeningHours;
import com.dvproject.vertTerm.repository.AvailabilityRepository;
import com.dvproject.vertTerm.repository.OpeningHoursRepository;

@Service
public class OpeningHoursServiceImp implements OpeningHoursService {
	@Autowired
	private OpeningHoursRepository repo;

	@Autowired
	private AvailabilityRepository availRepo;

	@Override
	public OpeningHours get() {
		List<OpeningHours> openingHours = repo.findAll();

		if (openingHours == null || openingHours.size() == 0)
			throw new RuntimeException("Two OpeningHourshave not been setup correctly, please restart the webapp");

		if (openingHours.size() > 1)
			throw new RuntimeException("Two OpeningHours objects in database!");

		return repo.findAll().get(0);
	}

	@Override
	public OpeningHours update(OpeningHours updatedInstance) {
		List<Availability> updatedInstanceAvailability = updatedInstance.getAvailabilities();
		updateInteral(updatedInstanceAvailability);
		
		OpeningHours openingHours = get();
		List<Availability> openingHoursAvailabilities = openingHours.getAvailabilities();
		Map<String, Availability> availabilitiesMap;

		// db-object: id -> Availability
		availabilitiesMap = openingHoursAvailabilities != null
				? openingHoursAvailabilities.stream()
						.collect(Collectors.toMap(Availability::getId, availability -> availability))
				: null;

		// add all new Availabilities 
		if (availabilitiesMap != null)
			openingHoursAvailabilities.addAll(updatedInstanceAvailability.stream()
					.filter(avail -> !availabilitiesMap.containsKey(avail.getId())).collect(Collectors.toList()));
		else 
			openingHours.setAvailabilities(updatedInstanceAvailability);

		repo.save(openingHours);

		return get();
	}

	public void updateInteral(List<Availability> availabilities) {
		Availability repoAvailability = null;

		for (int i = 0; i < availabilities.size(); i++) {
			Availability availability = availabilities.get(i);

			if (availability.getId() != null) {
				repoAvailability = availRepo.findById(availability.getId()).orElse(null);
				if (repoAvailability != null) {
					Date endOfSeries = availability.getEndOfSeries();
					// endOfSeries should be set
					if (repoAvailability.getEndOfSeries() == null && endOfSeries != null) {
						repoAvailability.setEndOfSeries(endOfSeries);
						availRepo.save(repoAvailability);
						availabilities.set(i, repoAvailability);
					}
				} else { // id != null && no entity in db
					availability.setId(null);
					repoAvailability = availRepo.save(availability);
				}
			} else // id == null
				repoAvailability = availRepo.save(availability);
		}
	}

	@Override
	public void deleteAvailabilitiesInThePast() {
		OpeningHours openingHours = get();
		List<Availability> openingHoursAvailabilities = openingHours.getAvailabilities();
		Date now = new Date();

		openingHours.setAvailabilities(openingHoursAvailabilities.stream()
				.filter(avail -> avail.getEndOfSeries() == null || avail.getEndOfSeries().after(now))
				.collect(Collectors.toList()));

		repo.save(openingHours);
	}

}

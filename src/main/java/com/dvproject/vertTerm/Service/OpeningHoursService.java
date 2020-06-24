package com.dvproject.vertTerm.Service;

import com.dvproject.vertTerm.Model.OpeningHours;

public interface OpeningHoursService {
	OpeningHours get();
	
	OpeningHours update(OpeningHours updatedInstance);
	
	void deleteAvailabilitiesInThePast();
}

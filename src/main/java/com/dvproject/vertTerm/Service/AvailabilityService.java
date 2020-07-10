package com.dvproject.vertTerm.Service;

import java.util.List;

import com.dvproject.vertTerm.Model.Availability;
import com.dvproject.vertTerm.Model.Available;

/**
 * @author Joshua Müller
 */
public interface AvailabilityService {
	List<Availability> getAllAvailabilities(String id);
	
	Available getById(String id);
}

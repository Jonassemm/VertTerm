package com.dvproject.vertTerm.Service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.dvproject.vertTerm.Model.Availability;
import com.dvproject.vertTerm.Model.Consumable;
import com.dvproject.vertTerm.Model.Status;
import com.dvproject.vertTerm.repository.ConsumableRepository;

/**
 * @author Joshua Müller
 */
@Service
public class ConsumableServiceImpl implements ConsumableService, AvailabilityService {
	@Autowired
	private ConsumableRepository consumableRepository;

	@Autowired
	private AvailabilityServiceImpl availabilityService;

	@Override
	@PreAuthorize("hasAuthority('RESOURCE_READ')")
	public List<Consumable> getAll() {
		return consumableRepository.findAll();
	}

	@Override
	@PreAuthorize("hasAuthority('RESOURCE_READ')")
	public List<Consumable> getAllWithStatus(Status status) {
		return consumableRepository.findByStatus(status);
	}

	@Override
	@PreAuthorize("hasAuthority('RESOURCE_READ')")
	public Consumable getById(String id) {
		return getConsumableFromDB(id);
	}

	@Override
	@PreAuthorize("hasAuthority('RESOURCE_READ')")
	public List<Availability> getAllAvailabilities(String id) {
		Consumable consumable = getById(id);

		if (consumable == null)
			throw new IllegalArgumentException("No consumable with the given id");
		
		return consumable.getAvailabilities();
	}

	@Override
	@PreAuthorize("hasAuthority('RESOURCE_WRITE')")
	public Consumable create(Consumable newInstance) {
		availabilityService.update(newInstance.getAvailabilities(), newInstance);
		return consumableRepository.save(newInstance);
	}

	@Override
	@PreAuthorize("hasAuthority('RESOURCE_WRITE')")
	public Consumable update(Consumable updatedInstance) {
		Optional<Consumable> Consumable = consumableRepository.findById(updatedInstance.getId());
		if (Consumable.isPresent()) {
			availabilityService.loadAllAvailabilitiesOfEntity(updatedInstance.getAvailabilities(), updatedInstance,
					this);
			return consumableRepository.save(updatedInstance);
		} else
			throw new ResourceNotFoundException(
					"Consumable with the given id :" + updatedInstance.getId() + " not found");
	}

	@Override
	@PreAuthorize("hasAuthority('RESOURCE_WRITE')")
	public boolean delete(String id) {
		this.deleteFromDB(id);

		return this.getConsumableFromDB(id).getStatus().isDeleted();
	}

	private Consumable getConsumableFromDB(String id) {
		if (id == null) {
			throw new NullPointerException("The id of the given consumable is null");
		}

		Optional<Consumable> consumable = consumableRepository.findById(id);

		if (consumable.isPresent()) {
			return consumable.get();
		} else {
			throw new ResourceNotFoundException("No consumable with the given id (" + id + ") can be found.");
		}
	}

	private Consumable deleteFromDB(String id) {
		Consumable consumable = getConsumableFromDB(id);

		consumable.setStatus(Status.DELETED);

		return consumableRepository.save(consumable);
	}

}

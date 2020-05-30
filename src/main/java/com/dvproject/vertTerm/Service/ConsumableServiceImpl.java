package com.dvproject.vertTerm.Service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import com.dvproject.vertTerm.Model.Consumable;
import com.dvproject.vertTerm.Model.Status;
import com.dvproject.vertTerm.repository.ConsumableRepository;

@Service
public class ConsumableServiceImpl implements ConsumableService {
	@Autowired
	private ConsumableRepository consumableRepository;

	@Override
	public List<Consumable> getAll() {
		return consumableRepository.findAll();
	}

	@Override
	public Consumable getById(String id) {
		return this.getConsumableFromDB(id);
	}

	@Override
	public Consumable create(Consumable newInstance) {
		return consumableRepository.save(newInstance);
	}

	@Override
	public Consumable update(Consumable updatedInstance) {
		return consumableRepository.save(updatedInstance);
	}

	@Override
	public boolean delete(String id) {
		this.deleteFromDB(id);
		
		return this.getConsumableFromDB(id).getStatus() == Status.DELETED;
	}

	private Consumable getConsumableFromDB (String id) {
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

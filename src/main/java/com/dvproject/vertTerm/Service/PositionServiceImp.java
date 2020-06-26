package com.dvproject.vertTerm.Service;

import com.dvproject.vertTerm.Model.Position;
import com.dvproject.vertTerm.Model.Status;
import com.dvproject.vertTerm.repository.PositionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PositionServiceImp implements PositionService {
	@Autowired
	private PositionRepository positionRepository;

	@Override
	public List<Position> getAll() {
		return positionRepository.findAll();
	}

	@Override
	public List<Position> getAll(Status status) {
		return positionRepository.findByStatus(status);
	}

	@Override
	public Position getById(String id) {
		Optional<Position> position = positionRepository.findById(id);
		return position.orElse(null);
	}

	@Override
	public Position update(Position position) {
		if (position.getId() != null && positionRepository.findById(position.getId()).isPresent()
				&& StatusService.isUpdateable(position.getStatus())) {
			position.setName(capitalize(position.getName()));
			return positionRepository.save(position);
		}
		return null;
	}

	@Override
	public boolean delete(String id) {
		positionRepository.delete(this.getPositionsInternal(id));
		return positionRepository.existsById(id);
	}

	@Override
	public List<Position> getPositions(String[] ids) {
		List<Position> positions = new ArrayList<>();

		for (String id : ids) {
			positions.add(this.getPositionsInternal(id));
		}

		return positions;
	}

	@Override
	public Position create(Position position) {
		if (position.getId() == null) {
			position.setName(capitalize(position.getName()));
			position.setStatus(Status.ACTIVE);
			return positionRepository.save(position);
		}
		if (positionRepository.findById(position.getId()).isPresent()) {
			throw new ResourceNotFoundException(
					"Procedure with the given id (" + position.getId() + ") exists on the database. Use the update method.");
		}
		return null;
	}

	private Position getPositionsInternal(String id) {
		if (id == null) { throw new ResourceNotFoundException("The id of the given procedure is null"); }

		Optional<Position> position = positionRepository.findById(id);
		if (position.isPresent()) {
			return position.get();
		} else {
			throw new ResourceNotFoundException("No procedure with the given id (" + id + ") can be found.");
		}
	}

	public static String capitalize(String str) {
		if (str == null)
			return str;
		return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();

	}
}

package com.dvproject.vertTerm.Service;

import com.dvproject.vertTerm.Model.Position;
import com.dvproject.vertTerm.Model.Status;

import java.util.List;

public interface PositionService extends BasicService<Position> {
	//GET
	List<Position> getAll(Status status);
	
	List<Position> getPositions(String [] ids);
}

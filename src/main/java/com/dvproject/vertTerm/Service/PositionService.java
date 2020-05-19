package com.dvproject.vertTerm.Service;

import java.util.List;

import com.dvproject.vertTerm.Model.Position;

public interface PositionService {
	//GET
	List<Position> getAllPositions ();
	
	List<Position> getPositions(String [] ids);
	
	//POST
	Position insertPosition(Position position);
	
	//PUT
	Position updatePosition(Position position);
	
	//DELETE
	boolean deletePosition(String id);
}

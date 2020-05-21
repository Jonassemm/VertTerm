package com.dvproject.vertTerm.Service;

import com.dvproject.vertTerm.Model.Position;

import java.util.List;

public interface PositionService extends Service<Position>{
	//GET
	List<Position> getPositions(String [] ids);
}

package com.dvproject.vertTerm.Service;

import com.dvproject.vertTerm.Model.Position;

import java.util.List;

public interface PositionService extends BasicService<Position> {
	//GET
	List<Position> getPositions(String [] ids);
}

package com.dvproject.vertTerm.Service;

import java.util.Date;
import java.util.List;

import com.dvproject.vertTerm.Model.Availability;
import com.dvproject.vertTerm.Model.Position;
import com.dvproject.vertTerm.Model.Procedure;
import com.dvproject.vertTerm.Model.ProcedureRelation;
import com.dvproject.vertTerm.Model.ResourceType;
import com.dvproject.vertTerm.Model.Restriction;

public interface ProcedureService {
	//GET
	List<Procedure> getAllProcedures();
	
	List<Procedure> getProcedures(String[] ids);

	List<ProcedureRelation> getPrecedingProcedures(String id);
	
	List<ProcedureRelation> getSubsequentProcedures(String id);

	List<ResourceType> getNeededResourceTypes(String id);

	List<Position> getNeededPositions(String id);
	
	List<Availability> getAllAvailabilities(String id);
	
	List<Restriction> getProcedureRestrictions(String id);
	
	boolean isDeleted(String id);
	
	boolean isAvailableBetween(String id, Date startdate, Date enddate);

	//POST
	Procedure insertProcedure(Procedure procedure);

	//PUT
	Procedure updateProcedure(Procedure procedure);
	
	Procedure updateProceduredata(Procedure procedure);

	List<ProcedureRelation> updatePrecedingProcedures(String id, List<ProcedureRelation> precedingProcedures);
	
	List<ProcedureRelation> updateSubsequentProcedures(String id, List<ProcedureRelation> subsequentProcedures);

	List<ResourceType> updateNeededResourceTypes(String id, List<ResourceType> resourceTypes);

	List<Position> updateNeededPositions(String id, List<Position> positions);
	
	List<Availability> updateAllAvailabilities(String id, List<Availability> availabilities);
	
	List<Restriction> updateProcedureRestrictions(String id, List<Restriction> restrictions);

	//DELETE
	Procedure deleteProcedure(String id);

}

package com.dvproject.vertTerm.Service;

import com.dvproject.vertTerm.Model.*;

import java.util.Date;
import java.util.List;

public interface ProcedureService extends BasicService <Procedure>{
	//GET
	List<Procedure> getAll(Status status);
	
	List<Procedure> getByIds(String[] ids);

	List<ProcedureRelation> getPrecedingProcedures(String id);
	
	List<ProcedureRelation> getSubsequentProcedures(String id);

	List<ResourceType> getNeededResourceTypes(String id);

	List<Position> getNeededPositions(String id);
	
	List<Availability> getAllAvailabilities(String id);
	
	List<Restriction> getProcedureRestrictions(String id);
	
	boolean isAvailableBetween(String id, Date startdate, Date enddate);
	
	boolean hasCorrectProcedureRelation(List<Appointment> appointments);
	
	boolean isConformingToPositionConditions(Procedure procedure, List<Employee> employees);
	
	boolean isConformingToResourceTypeConditions(Procedure procedure, List<Resource> resources);
	
	//PUT
	Procedure updateProceduredata(Procedure procedure);

	List<ProcedureRelation> updatePrecedingProcedures(String id, List<ProcedureRelation> precedingProcedures);
	
	List<ProcedureRelation> updateSubsequentProcedures(String id, List<ProcedureRelation> subsequentProcedures);

	List<ResourceType> updateNeededResourceTypes(String id, List<ResourceType> resourceTypes);

	List<Position> updateNeededPositions(String id, List<Position> positions);
	
	List<Availability> updateAvailabilities(String id, List<Availability> availabilities);
	
	List<Restriction> updateProcedureRestrictions(String id, List<Restriction> restrictions);

}

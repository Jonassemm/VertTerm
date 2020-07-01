package com.dvproject.vertTerm.Service;

import com.dvproject.vertTerm.Model.*;
import com.dvproject.vertTerm.repository.ProcedureRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ProcedureServiceImp implements ProcedureService, AvailabilityService {
	@Autowired
	private ProcedureRepository procedureRepository;

	@Autowired
	private AvailabilityServiceImpl availabilityService;

	@Override
	public List<Procedure> getAll() {
		return procedureRepository.findAll();
	}

	@Override
	public List<Procedure> getAll(Status status) {
		return procedureRepository.findByStatus(status);
	}
	
	public List<Procedure> getAll(Status status, boolean publicProcedure){
		return procedureRepository.findByStatusAndPublicProcedure(status, publicProcedure);
	}

	@Override
	public List<Procedure> getByIds(String[] ids) {
		return procedureRepository.findByIds(ids);
	}

	@Override
	public Procedure getById(String id) {
		return getProcedureFromDB(id);
	}

	@Override
	public List<ProcedureRelation> getPrecedingProcedures(String id) {
		return getProcedureFromDB(id).getPrecedingRelations();
	}

	@Override
	public List<ProcedureRelation> getSubsequentProcedures(String id) {
		return getProcedureFromDB(id).getSubsequentRelations();
	}

	@Override
	public List<ResourceType> getNeededResourceTypes(String id) {
		return this.getProcedureFromDB(id).getNeededResourceTypes();
	}

	@Override
	public List<Position> getNeededPositions(String id) {
		return this.getProcedureFromDB(id).getNeededEmployeePositions();
	}

	@Override
	public List<Availability> getAllAvailabilities(String id) {
		return this.getProcedureFromDB(id).getAvailabilities();
	}

	@Override
	public List<Restriction> getProcedureRestrictions(String id) {
		return this.getProcedureFromDB(id).getRestrictions();
	}

	@Override
	public boolean isAvailableBetween(String id, Date startdate, Date enddate) {
		return availabilityService.isAvailable(this.getProcedureFromDB(id).getAvailabilities(), startdate, enddate);
	}

	@Override
	public Procedure create(Procedure procedure) {
		if (procedure.getId() == null) {
			procedure.testAllReferenceValues();
			availabilityService.update(procedure.getAvailabilities(), procedure);
			procedure.setName(capitalize(procedure.getName()));
			return procedureRepository.save(procedure);
		}
		if (procedureRepository.findById(procedure.getId()).isPresent()) {
			throw new IllegalArgumentException("Procedure with the given id (" + procedure.getId()
					+ ") exists on the database. Use the update method.");
		}
		return procedure;
	}

	@Override
	public Procedure update(Procedure procedure) {
		Procedure oldProcedure = getProcedureFromDB(procedure.getId());
		
		procedure.testAllReferenceValues();
		procedure.setName(capitalize(procedure.getName()));
		availabilityService.loadAllAvailabilitiesOfEntity(procedure.getAvailabilities(), procedure, this);

		testUpdatebility(oldProcedure.getStatus());

		procedureRepository.save(procedure);
		
		return getProcedureFromDB(procedure.getId());
	}

	@Override
	public Procedure updateProceduredata(Procedure procedure) {
		Procedure oldProcedure = getProcedureFromDB(procedure.getId());

		procedure.testAllReferenceValues();
		testUpdatebility(procedure.getStatus());

		oldProcedure.setName(capitalize(procedure.getName()));
		oldProcedure.setDescription(procedure.getDescription());
		oldProcedure.setPricePerHour(procedure.getPricePerHour());
		oldProcedure.setPricePerInvocation(procedure.getPricePerInvocation());
		oldProcedure.setDuration(procedure.getDuration());

		return procedureRepository.save(oldProcedure);
	}

	@Override
	public List<ProcedureRelation> updatePrecedingProcedures(String id, List<ProcedureRelation> precedingProcedures) {
		Procedure procedure = getProcedureFromDB(id);

		procedure.testAllRelations();
		testUpdatebility(procedure.getStatus());

		procedure.setPrecedingRelations(precedingProcedures);
		procedureRepository.save(procedure);

		return getProcedureFromDB(id).getPrecedingRelations();
	}

	@Override
	public List<ProcedureRelation> updateSubsequentProcedures(String id, List<ProcedureRelation> subsequentProcedures) {
		Procedure procedure = getProcedureFromDB(id);

		procedure.testAllRelations();
		testUpdatebility(procedure.getStatus());

		procedure.setSubsequentRelations(subsequentProcedures);
		procedureRepository.save(procedure);

		return getProcedureFromDB(id).getSubsequentRelations();
	}

	@Override
	public List<ResourceType> updateNeededResourceTypes(String id, List<ResourceType> resourceTypes) {
		Procedure procedure = getProcedureFromDB(id);

		procedure.testResourceTypes();
		testUpdatebility(procedure.getStatus());
       
		procedure.setNeededResourceTypes(resourceTypes);

		procedureRepository.save(procedure);

		return getProcedureFromDB(id).getNeededResourceTypes();
	}

	@Override
	public List<Position> updateNeededPositions(String id, List<Position> positions) {
		Procedure procedure = getProcedureFromDB(id);

		procedure.testPositions();
		testUpdatebility(procedure.getStatus());
		 
		procedure.setNeededEmployeePositions(positions);
		
		procedureRepository.save(procedure);

		return getProcedureFromDB(id).getNeededEmployeePositions();
	}

	@Override
	public List<Availability> updateAvailabilities(String id, List<Availability> availabilities) {
		Procedure procedure = getProcedureFromDB(id);

		testUpdatebility(procedure.getStatus());
		procedure.setAvailabilities(availabilities);
		procedureRepository.save(procedure);

		return getProcedureFromDB(id).getAvailabilities();
	}

	@Override
	public List<Restriction> updateProcedureRestrictions(String id, List<Restriction> restrictions) {
		Procedure procedure = getProcedureFromDB(id);

		testUpdatebility(procedure.getStatus());
		 for(Restriction restr: restrictions)
	        {
			 restr.setName(capitalize(restr.getName()));
	        }
		procedure.setRestrictions(restrictions);
		procedureRepository.save(procedure);

		return getProcedureFromDB(id).getRestrictions();
	}

	@Override
	public boolean delete(String id) {
		this.deleteFromDB(id);

		return getProcedureFromDB(id).getStatus() == Status.DELETED;
	}

	private Procedure getProcedureFromDB(String id) {
		if (id == null) {
			throw new NullPointerException("The id of the given procedure is null");
		}

		Optional<Procedure> procedureDB = procedureRepository.findById(id);

		if (procedureDB.isPresent()) {
			return procedureDB.get();
		} else {
			throw new ResourceNotFoundException("No procedure with the given id (" + id + ") can be found.");
		}
	}

	private Procedure deleteFromDB(String id) {
		Procedure procedure = getProcedureFromDB(id);

		procedure.setStatus(Status.DELETED);

		return procedureRepository.save(procedure);
	}

	private void testUpdatebility(Status status) {
		if (!StatusService.isUpdateable(status))
			throw new IllegalArgumentException("The given procedure is not updateable");
	}
    public static String capitalize(String str)
    {
        if(str == null) return str;
        return  str.substring(0, 1).toUpperCase()+str.substring(1).toLowerCase();
        
    }
}

package com.dvproject.vertTerm.Service;

import com.dvproject.vertTerm.Model.*;
import com.dvproject.vertTerm.repository.ProcedureRepository;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProcedureServiceImp extends WarningServiceImpl implements ProcedureService, AvailabilityService {
	@Autowired
	private ProcedureRepository procedureRepository;

	@Autowired
	private AvailabilityServiceImpl availabilityService;

	@Autowired
	private AppointmentService appointmentService;

	@Override
	public List<Procedure> getAll() {
		return procedureRepository.findAll();
	}

	@Override
	public List<Procedure> getAll(Status status) {
		return procedureRepository.findByStatus(status);
	}

	public List<Procedure> getAll(Status status, boolean publicProcedure) {
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
		String procedureId = procedure.getId();
		Procedure oldProcedure = getProcedureFromDB(procedureId);

		procedure.testAllReferenceValues();
		procedure.setName(capitalize(procedure.getName()));
		availabilityService.loadAllAvailabilitiesOfEntity(procedure.getAvailabilities(), procedure, this);

		testUpdatebility(oldProcedure.getStatus());

		procedureRepository.save(procedure);

		testWarningsForAppointments(oldProcedure, procedure);

		return getProcedureFromDB(procedure.getId());
	}

	@Override
	public Procedure updateProceduredata(Procedure procedure) {
		String procedureId = procedure.getId();
		Procedure oldProcedure = getProcedureFromDB(procedureId);
		Procedure retVal = null;

		procedure.testAllReferenceValues();
		testUpdatebility(procedure.getStatus());

		oldProcedure.setName(capitalize(procedure.getName()));
		oldProcedure.setDescription(procedure.getDescription());
		oldProcedure.setPricePerHour(procedure.getPricePerHour());
		oldProcedure.setPricePerInvocation(procedure.getPricePerInvocation());
		oldProcedure.setDuration(procedure.getDuration());

		retVal = procedureRepository.save(oldProcedure);

		if (durationIsDifferent(procedure, oldProcedure))
			testWarningsFor(procedureId);

		return retVal;
	}

	@Override
	public List<ProcedureRelation> updatePrecedingProcedures(String id, List<ProcedureRelation> precedingProcedures) {
		Procedure procedure = getProcedureFromDB(id);

		procedure.testAllRelations();
		testUpdatebility(procedure.getStatus());

		procedure.setPrecedingRelations(precedingProcedures);
		procedureRepository.save(procedure);

		testWarningsForRelations(procedure.getPrecedingRelations(), precedingProcedures);
		testWarningsFor(id);

		return getProcedureFromDB(id).getPrecedingRelations();
	}

	@Override
	public List<ProcedureRelation> updateSubsequentProcedures(String id, List<ProcedureRelation> subsequentRelations) {
		Procedure procedure = getProcedureFromDB(id);

		procedure.testAllRelations();
		testUpdatebility(procedure.getStatus());

		procedure.setSubsequentRelations(subsequentRelations);
		procedureRepository.save(procedure);

		testWarningsForRelations(procedure.getSubsequentRelations(), subsequentRelations);
		testWarningsFor(id);

		return getProcedureFromDB(id).getSubsequentRelations();
	}

	@Override
	public List<ResourceType> updateNeededResourceTypes(String id, List<ResourceType> resourceTypes) {
		Procedure procedure = getProcedureFromDB(id);

		procedure.testResourceTypes();
		testUpdatebility(procedure.getStatus());

		procedure.setNeededResourceTypes(resourceTypes);

		procedureRepository.save(procedure);

		testWarningsForResourceType(procedure, resourceTypes);

		return getProcedureFromDB(id).getNeededResourceTypes();
	}

	@Override
	public List<Position> updateNeededPositions(String id, List<Position> positions) {
		Procedure procedure = getProcedureFromDB(id);

		procedure.testPositions();
		testUpdatebility(procedure.getStatus());

		procedure.setNeededEmployeePositions(positions);

		procedureRepository.save(procedure);
		
		testWarningsForPosition(procedure, positions);

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
		for (Restriction restr : restrictions) {
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

	public void testWarningsForAppointments(Procedure oldProcedure, Procedure newProcedure) {
		testWarningsFor(oldProcedure.getId());

		testWarningsForProcedureRelations(oldProcedure, newProcedure);

		testWarningsForResourceType(oldProcedure, newProcedure);
		
		testWarningsForPositions(oldProcedure, newProcedure);
	}

	private Procedure getProcedureFromDB(String id) {
		if (id == null) { throw new NullPointerException("The id of the given procedure is null"); }

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

	private boolean durationIsDifferent(Procedure procedure1, Procedure procedure2) {
		Duration duration1 = procedure1.getDuration();
		Duration duration2 = procedure2.getDuration();

		if (duration1 == null && duration2 == null)
			return false;
		else
			if (duration1 == null ^ duration2 == null)
				return true;
			else
				return duration1.toMillis() != duration2.toMillis();
	}

	private void testWarningsForProcedureRelations(Procedure oldProcedure, Procedure newProcedure) {
		testWarningsForRelations(oldProcedure.getPrecedingRelations(), newProcedure.getPrecedingRelations());
		testWarningsForRelations(oldProcedure.getSubsequentRelations(), newProcedure.getSubsequentRelations());
	}

	private void testWarningsForRelations(List<ProcedureRelation> oldRelations, List<ProcedureRelation> newRelations) {
		List<ProcedureRelation> procedureRelations = getListOfChanged(oldRelations, newRelations);
		List<String> procedureIds = new ArrayList<>();

		procedureIds = procedureRelations.stream().map(relation -> relation.getProcedure().getId()).distinct()
				.collect(Collectors.toList());

		testWarningsFor(procedureIds);
	}

	private void testWarningsForResourceType(Procedure oldProcedure, Procedure newProcedure) {
		testWarningsForResourceType(oldProcedure, newProcedure.getNeededResourceTypes());
	}

	private void testWarningsForResourceType(Procedure procedure, List<ResourceType> resourceTypes) {
		List<ResourceType> changedResourceTypes = getListOfChanged(procedure.getNeededResourceTypes(), resourceTypes);
		List<ObjectId> resourceTypeIds = changedResourceTypes.stream().map(resType -> new ObjectId(resType.getId()))
				.distinct().collect(Collectors.toList());
		List<Procedure> procedures = procedureRepository.findByNeededResourceTypesIdIn(resourceTypeIds);
		List<String> procedureIds = procedures.stream().map(proc -> proc.getId()).collect(Collectors.toList());

		testWarningsFor(procedureIds);
	}
	
	private void testWarningsForPositions(Procedure oldProcedure, Procedure newProcedure) {
		testWarningsForPosition(oldProcedure, newProcedure.getNeededEmployeePositions());
	}
	
	private void testWarningsForPosition(Procedure procedure, List<Position> positions) {
		List<Position> changedPositions = getListOfChanged(procedure.getNeededEmployeePositions(), positions);
		List<ObjectId> positionIds = changedPositions.stream().map(position -> new ObjectId(position.getId()))
				.distinct().collect(Collectors.toList());
		List<Procedure> procedures = procedureRepository.findByNeededEmployeePositionsIn(positionIds);
		List<String> procedureIds = procedures.stream().map(proc -> proc.getId()).collect(Collectors.toList());

		testWarningsFor(procedureIds);
	}

	public static String capitalize(String str) {
		if (str == null)
			return str;
		return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();

	}

	@Override
	List<Appointment> getPlannedAppointmentsWithId(String id) {
		return appointmentService.getAppointmentsByProcedureIdAndAppointmentStatus(id, AppointmentStatus.PLANNED);
	}
}

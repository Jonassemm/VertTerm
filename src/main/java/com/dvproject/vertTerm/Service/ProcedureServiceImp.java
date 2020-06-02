package com.dvproject.vertTerm.Service;

import com.dvproject.vertTerm.Model.*;
import com.dvproject.vertTerm.repository.ProcedureRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;

@Service
public class ProcedureServiceImp implements ProcedureService {
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
	public boolean hasCorrectProcedureRelations(List<Appointment> appointmentsToTest) {
		// procedure.id -> appointment
		Map<String, Appointment> appointments = new HashMap<>();
		// procedure.id -> procedure
		Map<String, Procedure> procedures = new HashMap<>();

		// populate Maps
		for (Appointment appointment : appointmentsToTest) {
			String id = appointment.getBookedProcedure().getId();
			procedures.put(id, this.getProcedureFromDB(id));

			appointments.put(id, appointment);
		}

		for (Appointment appointment : appointmentsToTest) {
			Procedure procedure = procedures.get(appointment.getBookedProcedure().getId());
			List<ProcedureRelation> precedingprocedures = procedure.getPrecedingRelations();
			List<ProcedureRelation> subsequentprocedures = procedure.getSubsequentRelations();

			// test all precedingRelations
			if (precedingprocedures != null) {
				for (ProcedureRelation procedureRelation : precedingprocedures) {
					if (procedures.containsKey(procedureRelation.getProcedure().getId())) {
						Appointment appointmentToTest = appointments.get(procedureRelation.getProcedure().getId());

						if (doAppointmentsConformToProcedureRelation(appointmentToTest, appointment, procedureRelation))
							continue;
					}

					return false;
				}
			}

			// test all subsequentRelations
			if (subsequentprocedures != null) {
				for (ProcedureRelation procedureRelation : subsequentprocedures) {
					if (procedures.containsKey(procedureRelation.getProcedure().getId())) {
						Appointment appointmentToTest = appointments.get(procedureRelation.getProcedure().getId());

						if (doAppointmentsConformToProcedureRelation(appointment, appointmentToTest, procedureRelation))
							continue;
					}

					return false;
				}
			}
		}

		return true;
	}
	
	@Override
	public boolean hasCorrectEmployees(Procedure procedure, List<Employee> employees) {
		boolean testVal = false;
		List<Position> procedurePositions = procedure.getNeededEmployeePositions();
		
		for (int i = 0; i < employees.size(); i++) {
			Employee employee = employees.get(i);
			Position procedurePosition = procedurePositions.get(i);
			
			List<Position> positions = employee.getPositions();
			
			for (Position position : positions) {
				if (position.getId().equals(procedurePosition.getId())) {
					testVal = true;
					break;
				}
			}
			
			if (!testVal)
				return false;
			
			testVal = false;
		}
		
		return true;
	}

	@Override
	public boolean hasCorrectResources(Procedure procedure, List<Resource> resources) {
		boolean testVal = false;
		List<ResourceType> procedureResourceTypes = procedure.getNeededResourceTypes();
		
		for (int i = 0; i < resources.size(); i++) {
			Resource resource = resources.get(i);
			ResourceType procedureResourceType = procedureResourceTypes.get(i);
			
			List<ResourceType> resourceTypes = resource.getResourceTypes();
			
			for (ResourceType presourceType : resourceTypes) {
				if (presourceType.getId().equals(procedureResourceType.getId())) {
					testVal = true;
					break;
				}
			}
			
			if (!testVal)
				return false;
			
			testVal = false;
		}
		
		return true;
	}

	@Override
	public Procedure create(Procedure procedure) {
		if (procedure.getId() == null) {
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

		testUpdatebility(oldProcedure.getStatus());

		return procedureRepository.save(procedure);
	}

	@Override
	public Procedure updateProceduredata(Procedure procedure) {
		Procedure oldProcedure = getProcedureFromDB(procedure.getId());

		testUpdatebility(procedure.getStatus());

		oldProcedure.setName(procedure.getName());
		oldProcedure.setDescription(procedure.getDescription());
		oldProcedure.setPricePerHour(procedure.getPricePerHour());
		oldProcedure.setPricePerInvocation(procedure.getPricePerInvocation());
		oldProcedure.setDurationInMinutes(procedure.getDurationInMinutes());

		return procedureRepository.save(oldProcedure);
	}

	@Override
	public List<ProcedureRelation> updatePrecedingProcedures(String id, List<ProcedureRelation> precedingProcedures) {
		Procedure procedure = getProcedureFromDB(id);

		testUpdatebility(procedure.getStatus());

		procedure.setPrecedingRelations(precedingProcedures);
		procedureRepository.save(procedure);

		return getProcedureFromDB(id).getPrecedingRelations();
	}

	@Override
	public List<ProcedureRelation> updateSubsequentProcedures(String id, List<ProcedureRelation> subsequentProcedures) {
		Procedure procedure = getProcedureFromDB(id);

		testUpdatebility(procedure.getStatus());

		procedure.setSubsequentRelations(subsequentProcedures);
		procedureRepository.save(procedure);

		return getProcedureFromDB(id).getSubsequentRelations();
	}

	@Override
	public List<ResourceType> updateNeededResourceTypes(String id, List<ResourceType> resourceTypes) {
		Procedure procedure = getProcedureFromDB(id);

		testUpdatebility(procedure.getStatus());

		procedure.setNeededResourceTypes(resourceTypes);
		procedureRepository.save(procedure);

		return getProcedureFromDB(id).getNeededResourceTypes();
	}

	@Override
	public List<Position> updateNeededPositions(String id, List<Position> positions) {
		Procedure procedure = getProcedureFromDB(id);

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
		if (!StatusService.isUpdateable(status)) {
			throw new IllegalArgumentException("The given procedure is not updateable");
		}
	}

	private Calendar getCalendar(Date date) {
		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		calendar.setTime(date);
		return calendar;
	}

	private boolean doAppointmentsConformToProcedureRelation(Appointment startAppointment, Appointment endAppointment,
			ProcedureRelation procedureRelation) {
		// the duration between startAppointment.plannedEndtime and
		// endAppointment.plannedStarttime
		Duration timeBetween = Duration.between(getCalendar(startAppointment.getPlannedEndtime()).toInstant(),
				getCalendar(endAppointment.getPlannedStarttime()).toInstant());

		// minDifference <= timeBetween && timeBetween <= maxDifference
		return procedureRelation.getMinDifference().compareTo(timeBetween) <= 0
				&& timeBetween.compareTo(procedureRelation.getMaxDifference()) <= 0;
	}

}

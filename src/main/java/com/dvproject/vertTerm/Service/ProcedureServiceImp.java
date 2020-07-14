package com.dvproject.vertTerm.Service;

import com.dvproject.vertTerm.Model.*;
import com.dvproject.vertTerm.repository.ProcedureRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author Joshua Müller
 */
@Service
public class ProcedureServiceImp extends WarningServiceImpl implements ProcedureService, AvailabilityService {
	@Autowired
	private ProcedureRepository procedureRepository;

	@Autowired
	private AvailabilityServiceImpl availabilityService;

	@Autowired
	private AppointmentService appointmentService;

	@Override
	@PreAuthorize("hasAuthority('PROCEDURE_READ')")
	public List<Procedure> getAll() {
		return procedureRepository.findAll();
	}

	@Override
	@PreAuthorize("hasAuthority('PROCEDURE_READ')")
	public List<Procedure> getAll(Status status) {
		return procedureRepository.findByStatus(status);
	}

	@Override
	public List<Procedure> getAll(Status status, boolean publicProcedure) {
		return procedureRepository.findByStatusAndPublicProcedure(status, publicProcedure);
	}

	@Override
	@PreAuthorize("hasAuthority('PROCEDURE_READ')")
	public List<Procedure> getByIds(String[] ids) {
		return procedureRepository.findByIds(ids);
	}

	@Override
	@PreAuthorize("hasAuthority('PROCEDURE_READ')")
	public Procedure getById(String id) {
		return getProcedureFromDB(id);
	}

	@Override
	public List<Availability> getAllAvailabilities(String id) {
		return this.getProcedureFromDB(id).getAvailabilities();
	}

	@Override
	public boolean isAvailableBetween(String id, Date startdate, Date enddate) {
		return availabilityService.isAvailable(this.getProcedureFromDB(id).getAvailabilities(), startdate, enddate);
	}

	@Override
	@PreAuthorize("hasAuthority('PROCEDURE_WRITE')")
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
	@PreAuthorize("hasAuthority('PROCEDURE_WRITE')")
	public Procedure update(Procedure procedure) {
		String procedureId = procedure.getId();
		Procedure oldProcedure = getProcedureFromDB(procedureId);

		procedure.testAllReferenceValues();
		procedure.setName(capitalize(procedure.getName()));
		availabilityService.loadAllAvailabilitiesOfEntity(procedure.getAvailabilities(), procedure, this);

		testUpdatebility(oldProcedure.getStatus());

		procedureRepository.save(procedure);

		testWarningsFor(procedureId);

		return getProcedureFromDB(procedure.getId());
	}

	@Override
	@PreAuthorize("hasAuthority('PROCEDURE_WRITE')")
	public boolean delete(String id) {
		this.deleteFromDB(id);

		return getProcedureFromDB(id).getStatus() == Status.DELETED;
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

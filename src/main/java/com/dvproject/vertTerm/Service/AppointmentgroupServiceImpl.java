package com.dvproject.vertTerm.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import com.dvproject.vertTerm.Model.Appointment;
import com.dvproject.vertTerm.Model.AppointmentStatus;
import com.dvproject.vertTerm.Model.Appointmentgroup;
import com.dvproject.vertTerm.Model.Employee;
import com.dvproject.vertTerm.Model.NormalBookingTester;
import com.dvproject.vertTerm.Model.Optimizationstrategy;
import com.dvproject.vertTerm.Model.OverrideBookingTester;
import com.dvproject.vertTerm.Model.Procedure;
import com.dvproject.vertTerm.Model.Resource;
import com.dvproject.vertTerm.Model.Status;
import com.dvproject.vertTerm.Model.User;
import com.dvproject.vertTerm.Model.Warning;
import com.dvproject.vertTerm.exception.ProcedureException;
import com.dvproject.vertTerm.exception.ProcedureRelationException;
import com.dvproject.vertTerm.repository.AppointmentgroupRepository;

@Service
public class AppointmentgroupServiceImpl implements AppointmentgroupService {
	@Autowired
	private AppointmentgroupRepository appointmentgroupRepository;

	@Autowired
	private AppointmentServiceImpl appointmentService;

	@Autowired
	private ProcedureService procedureService;

	@Autowired
	private ResourceService resourceService;

	@Autowired
	private EmployeeService employeeService;

	@Autowired
	private UserService userService;

	@Autowired
	private RestrictionService restrictionService;

	@Override
	public List<Appointmentgroup> getAll() {
		return appointmentgroupRepository.findAll();
	}

	@Override
	public List<Appointmentgroup> getAppointmentgroupsWithStatus(Status status) {
		return appointmentgroupRepository.findByStatus(status);
	}

	@Override
	public Appointmentgroup getAppointmentgroupContainingAppointmentID(String id) {
		if (id == null) {
			throw new NullPointerException("The id of the given appointment is null");
		}
		if (appointmentService.getById(id) != null) {
			throw new ResourceNotFoundException("The id of the given appointment is invalid");
		}

		return appointmentgroupRepository.findByAppointmentsId(id);
	}

	@Override
	public Appointmentgroup getOptimizedSuggestion(Appointmentgroup appointmentgroup,
			Optimizationstrategy optimizationstrategy) {
		// TODO
		return null;
	}

	@Override
	public Appointmentgroup getById(String id) {
		return this.getAppointmentInternal(id);
	}

	@Override
	public Appointmentgroup create(Appointmentgroup newInstance) {
		if (!appointmentgroupRepository.existsById(newInstance.getId())) {
			List<Appointment> appointments = newInstance.getAppointments();

			for (Appointment appointment : appointments) {
				if (appointment.getPlannedStarttime() != null || appointment.getPlannedStarttime() != null
						|| appointment.getActualStarttime() != null || appointment.getActualEndtime() != null) {
					throw new IllegalArgumentException(
							"Appointments have already been booked, no new appointmentgroup can be created with them");
				}
			}

			return appointmentgroupRepository.save(newInstance);
		}
		return null;
	}

	@Override
	public Appointmentgroup update(Appointmentgroup updatedInstance) {
		if (appointmentgroupRepository.existsById(updatedInstance.getId())) {
			if (!StatusService.isUpdateable(updatedInstance.getStatus())) {
				throw new IllegalArgumentException("The given procedure is not updateable");
			}
			return appointmentgroupRepository.save(updatedInstance);
		}
		return null;
	}

	/**
	 * 
	 * @return true if the appointmentgroup has been booked
	 * 
	 * @exception RuntimeException if a value of the appointmentgroup does not
	 *                             conform to the conditions
	 */
	@Override
	public boolean bookAppointmentgroup(String userid, Appointmentgroup appointmentgroup, boolean override) {
		List<Appointment> appointments = appointmentgroup.getAppointments();
		boolean noUserAttached = userid.equals("");
		User user = noUserAttached ? userService.getAnonymousUser() : userService.getById(userid);

		for (Appointment appointment : appointments) {
			if (!noUserAttached && appointment.getBookedCustomer() != null
					&& !appointment.getBookedCustomer().getId().equals(userid)) {

				if (override) {
					appointment.addWarning(Warning.USER_WARNING);
				} else {
					throw new IllegalArgumentException(
							"User in the appointment for the procedure " + appointment.getBookedProcedure().getId()
									+ " does not conform to the given user for all appointments");
				}
			} else {
				appointment.setBookedCustomer(user);
			}

			try {
				loadAppointmentdataFromDatabase(appointment);
			} catch (ProcedureException ex) {
				if (!override)
					throw ex;

				appointment.addWarning(Warning.PROCEDURE_WARNING);
			}
		}

		if (override) {
			try {
				appointmentgroup.testProcedureRelations();
			} catch (ProcedureRelationException ex) {
				for (Appointment appointment : appointmentgroup.getAppointments()) {
					appointment.addWarning(Warning.PROCEDURE_RELATION_WARNING);
				}
			}

			appointmentgroup.testBookability(restrictionService, appointmentService, new OverrideBookingTester());
		} else {
			appointmentgroup.testProcedureRelations();
			appointmentgroup.testBookability(restrictionService, appointmentService, new NormalBookingTester());
		}

		if (noUserAttached) {
			userService.create(user);
		}

		// create all appointments of the appointmentgroup
		for (Appointment appointment : appointments) {
			appointment.setStatus(AppointmentStatus.PLANNED);
			appointmentService.create(appointment);
		}

		appointmentgroup.setStatus(Status.ACTIVE);
		appointmentgroupRepository.save(appointmentgroup);

		return true;
	}

	@Override
	// TODO
	public Appointment shiftAppointment(String appointmentId, Date startdate, Date enddate) {
		return null;
	}

	@Override
	public boolean delete(String id) {
		this.deleteAppointmentgroup(id);

		Appointmentgroup appointmentgroup = this.getAppointmentInternal(id);

		return appointmentgroup.getStatus() == Status.DELETED;
	}

	private Appointmentgroup deleteAppointmentgroup(String id) {
		Appointmentgroup appointmentgroup = this.getAppointmentInternal(id);
		appointmentgroup.setStatus(Status.DELETED);
		return appointmentgroupRepository.save(appointmentgroup);
	}

	private Appointmentgroup getAppointmentInternal(String id) {
		if (id == null) {
			throw new ResourceNotFoundException("The id of the given appointmentgroup is null");
		}

		Optional<Appointmentgroup> appointmentgroup = appointmentgroupRepository.findById(id);

		if (appointmentgroup.isPresent()) {
			return appointmentgroup.get();
		} else {
			throw new ResourceNotFoundException("No appointmentgroup with the given id (" + id + ") can be found.");
		}
	}

	private void loadAppointmentdataFromDatabase(Appointment appointment) {
		Procedure procedure = procedureService.getById(appointment.getBookedProcedure().getId());
		List<Employee> employees = new ArrayList<>();
		List<Resource> resources = new ArrayList<>();

		// populate list of employees
		appointment.getBookedEmployees().forEach(employee -> employees.add(employeeService.getById(employee.getId())));
		// set employees of appointment
		appointment.setBookedEmployees(employees);

		// populate list of resources
		appointment.getBookedResources().forEach(resource -> resources.add(resourceService.getById(resource.getId())));
		// set resources of appointment
		appointment.setBookedResources(resources);

		appointment.setBookedProcedure(procedure);

		if (hasActualTimeValue(appointment)) {
			throw new ProcedureException("Appointment of an procedure contains actual times", procedure);
		}
	}

	private boolean hasActualTimeValue(Appointment appointment) {
		return appointment.getActualStarttime() != null || appointment.getActualEndtime() != null;
	}

}

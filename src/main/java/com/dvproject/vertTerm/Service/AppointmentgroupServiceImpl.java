package com.dvproject.vertTerm.Service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import com.dvproject.vertTerm.Model.Appointment;
import com.dvproject.vertTerm.Model.Appointmentgroup;
import com.dvproject.vertTerm.Model.Employee;
import com.dvproject.vertTerm.Model.Optimizationstrategy;
import com.dvproject.vertTerm.Model.Procedure;
import com.dvproject.vertTerm.Model.Resource;
import com.dvproject.vertTerm.Model.Status;
import com.dvproject.vertTerm.Model.User;
import com.dvproject.vertTerm.repository.AppointmentgroupRepository;

@Service
public class AppointmentgroupServiceImpl implements AppointmentgroupService {
	@Autowired
	private AppointmentgroupRepository appointmentgroupRepository;

	@Autowired
	private AppointmentgroupService appointmentService;

	@Autowired
	private ProcedureService procedureService;

	@Autowired
	private UserService userService;

	@Override
	public List<Appointmentgroup> getAll() {
		return appointmentgroupRepository.findAll();
	}

	@Override
	public List<Appointmentgroup> getAppointmentgroupsWithStatus(Status status) {
		return appointmentgroupRepository.findByStatus(status);
	}

	@Override
	public Appointmentgroup getAppointmentgroupWithAppointmentID(String id) {
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
	public boolean bookAppointmentgroup(String userid, Appointmentgroup appointmentgroup) {
		List<Appointment> appointments = appointmentgroup.getAppointments();
		User user;
		boolean noUserAttached = userid == null || userid.equals("");

		if (noUserAttached) {
			// Method in user-Service needed
			user = this.createNewAnonymousUser();
		} else {
			user = userService.getById(userid);
		}

		checkProcedureRelation(appointments);

		for (Appointment appointment : appointments) {
			Procedure procedure = appointment.getBookedProcedure();

			checkEmployees(procedure, appointment.getBookedEmployees());

			checkResources(procedure, appointment.getBookedResources());

			checkAvailabilityOfProcedure(appointment);

			// TODO:
			// - ressourceService.isAvailable
			// - employeeService.isAvailable
			// - warning-tests in:
			// ressources/procedure
			// - test, whether the employees/resources are blocked in the planned time
			// interval

			if (noUserAttached) {
				appointment.setBookedCustomer(user);
			} else {
				if (!appointment.getBookedCustomer().getId().equals(userid)) {
					throw new IllegalArgumentException(
							"User in the appointment for the procedure " + appointment.getBookedProcedure().getId()
									+ " does not conform to the given user for all appointments");
				}
			}
		}

		if (noUserAttached) {
			userService.create(user);
		}
		
		for (Appointment appointment : appointments) {
			//book each appointment
			
		}

		return true;
	}

	@Override
	public boolean delete(String id) {
		this.deleteAppointmentgroup(id);

		Appointmentgroup appointmentgroup = this.getAppointmentInternal(id);

		return appointmentgroup.getStatus() == Status.DELETED;
	}

	/**
	 * 
	 * @return a new anonymousUser
	 */
	private User createNewAnonymousUser() {
		return userService.getAnonymousUser();
	}

	/**
	 * tests, whether the given appointment-list conforms to the relations of the
	 * procedure
	 * 
	 * @param appointments the list of appointments to test
	 * 
	 * @exception RuntimeException if the relations do not conform
	 */
	private void checkProcedureRelation(List<Appointment> appointments) {
		if (!procedureService.hasCorrectProcedureRelations(appointments))
			throw new RuntimeException(
					"Appointments can not be booked, because they do not conform to the procedure relations");
	}

	/**
	 * tests, whether the {@link Appointment#bookedEmployees employees} of the
	 * appointment conform to the {@link Procedure#neededEmployeePositions
	 * positions} in the procedure
	 * 
	 * @param procedure procedure of an appointment
	 * @param employees employees of an appointment
	 * 
	 * @exception RuntimeException if the employees do not match the positions
	 */
	private void checkEmployees(Procedure procedure, List<Employee> employees) {
		if (!procedureService.hasCorrectEmployees(procedure, employees))
			throw new RuntimeException(
					"Appointments can not be booked, because they do not conform to the position in the procedure "
							+ procedure.getName());
	}

	/**
	 * tests, whether the {@link Appointment#bookedResources resources} of the
	 * appointment conform to the {@link Procedure#neededResourceTypes
	 * resourceTypes} in the procedure
	 * 
	 * @param procedure procedure of an appointment
	 * @param resources resources of an appointment
	 * 
	 * @exception RuntimeException if the resources do not match the resourceTypes
	 */
	private void checkResources(Procedure procedure, List<Resource> resources) {
		if (!procedureService.hasCorrectResources(procedure, resources))
			throw new RuntimeException(
					"Appointments can not be booked, because they do not conform to the resourceType in the procedure "
							+ procedure.getName());
	}

	/**
	 * tests, whether the procedure has a availability in the time interval of the
	 * planned time interval of the appointment
	 * 
	 * @param appointment appointment that is to be booked
	 * 
	 * @exception RuntimeException if there is not availability
	 */
	private void checkAvailabilityOfProcedure(Appointment appointment) {
		if (!procedureService.isAvailableBetween(appointment.getBookedProcedure().getId(),
				appointment.getPlannedStarttime(), appointment.getPlannedEndtime()))
			throw new RuntimeException(
					"Appointments can not be booked, because the procedure is not available in the given time interval");
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

}

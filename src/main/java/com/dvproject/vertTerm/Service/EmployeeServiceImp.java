package com.dvproject.vertTerm.Service;

import com.dvproject.vertTerm.Model.Appointment;
import com.dvproject.vertTerm.Model.AppointmentStatus;
import com.dvproject.vertTerm.Model.Availability;
import com.dvproject.vertTerm.Model.Employee;
import com.dvproject.vertTerm.Model.Status;
import com.dvproject.vertTerm.Model.Warning;
import com.dvproject.vertTerm.repository.EmployeeRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class EmployeeServiceImp extends WarningServiceImpl implements EmployeeService, AvailabilityService {

	@Autowired
	EmployeeRepository repo;

	@Autowired
	private UserService userService;

	@Autowired
	private AvailabilityServiceImpl availabilityService;

	@Autowired
	private AppointmentService appointmentService;

	@Override
	public List<Employee> getAll() {
		return repo.findAll();
	}

	// @PreAuthorize("hasAuthority('USERS_DATA_READ')")
	public List<Employee> getAll(Status status) {
		List<Employee> users = null;
		switch (status) {
			case ACTIVE:
				users = repo.findAllActive();
				break;
			case INACTIVE:
				users = repo.findAllInactive();
				break;
			case DELETED:
				users = repo.findAllDeleted();
				break;
		}
		return (users);
	}

	public List<Employee> getActiveEmployeesByPositionId(String positionId) {
		// get all Active Employees from type
		List<Employee> employees = new ArrayList<>();
		List<Employee> AllEmployees = this.getAll(positionId);
		for (Employee emp : AllEmployees) {
			if (emp.getSystemStatus().equals(Status.ACTIVE)) {
				if (!employees.contains(emp))
					employees.add(emp);

			}

		}

		return employees;
	}

	public List<Employee> getAll(String positionId) {
		return repo.findByPositionsId(positionId);
	}

	@Override
	public Employee getById(String id) {
		Optional<Employee> appointment = repo.findById(id);
		return appointment.orElse(null);
	}

	@Override
	public Employee getByUsername(String username) {
		Optional<Employee> appointment = repo.findByUsername(username);
		return appointment.orElse(null);
	}

	@Override
	public List<Availability> getAllAvailabilities(String id) {
		Employee employee = this.getById(id);

		if (employee == null) { throw new IllegalArgumentException("No employee with the given id"); }

		return employee.getAvailabilities();
	}

	@Override
	public Employee create(Employee newInstance) {
		if (newInstance.getId() == null) {
			userService.testMandatoryFields(newInstance);
			userService.encodePassword(newInstance);
			newInstance.setFirstName(capitalize(newInstance.getFirstName()));
			newInstance.setLastName(capitalize(newInstance.getLastName()));
			availabilityService.update(newInstance.getAvailabilities(), newInstance);
			return repo.save(newInstance);
		}
		if (repo.findById(newInstance.getId()).isPresent()) {
			throw new ResourceNotFoundException("Instance with the given id (" + newInstance.getId()
					+ ") exists on the database. Use the update method.");
		}
		return null;
	}

	@Override
	public Employee update(Employee updatedInstance) {
		String employeeId = updatedInstance.getId();
		Optional<Employee> employee = employeeId != null ? repo.findById(employeeId) : null;
		Employee retVal = null;

		if (employeeId != null && employee.isPresent()) {
			userService.testMandatoryFields(updatedInstance);
			userService.encodePassword(updatedInstance);
			updatedInstance.setFirstName(capitalize(updatedInstance.getFirstName()));
			updatedInstance.setLastName(capitalize(updatedInstance.getLastName()));
			availabilityService.loadAllAvailabilitiesOfEntity(updatedInstance.getAvailabilities(), updatedInstance, this);

			retVal = repo.save(updatedInstance);

			testWarningsFor(employeeId);
			userService.testWarningsFor(updatedInstance.getId());
		}

		return retVal;
	}

	@Override
	public boolean delete(String id) {
		Employee user = getById(id);

		userService.testAppointments(id);
		testAppointments(id);
		user.obfuscate();

		user.setSystemStatus(Status.DELETED);
		repo.save(user);
		
		getPlannedAppointmentsWithId(id).forEach(app -> {
			app.addWarning(Warning.EMPLOYEE_WARNING);
			appointmentService.update(app);
		});

		return getById(id).getSystemStatus() == Status.DELETED;
	}

	@Override
	public boolean isEmployeeAvailableBetween(String id, Date startdate, Date enddate) {
		Employee Emp = getById(id);
		return availabilityService.isAvailable(Emp.getAvailabilities(), startdate, enddate);
	}

	public static String capitalize(String str) {
		if (str == null)
			return str;
		return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();

	}

	public void testAppointments(String id) {
		List<Appointment> appointments = appointmentService.getAppointmentsByEmployeeIdAndAppointmentStatus(id,
				AppointmentStatus.PLANNED);

		if (appointments != null && appointments.size() > 0)
			throw new IllegalArgumentException("Employee can not be deleted because he is used as a bookedEmployee");
	}

	@Override
	public List<Appointment> getPlannedAppointmentsWithId(String id) {
		return appointmentService.getAppointmentsByEmployeeIdAndAppointmentStatus(id, AppointmentStatus.PLANNED);
	}
}

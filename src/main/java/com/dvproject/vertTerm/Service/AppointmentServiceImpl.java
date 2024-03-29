package com.dvproject.vertTerm.Service;

import com.dvproject.vertTerm.Model.*;
import com.dvproject.vertTerm.exception.AppointmentTimeException;
import com.dvproject.vertTerm.exception.ProcedureException;
import com.dvproject.vertTerm.repository.*;
import com.dvproject.vertTerm.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import org.bson.types.ObjectId;


/** nur Methode "getAvailableResourcesAndEmployees" : author Amar Alkhankan  **/

@Service
public class AppointmentServiceImpl implements AppointmentService {

	@Autowired
	AppointmentRepository repo;
	
	@Autowired
	AppointmentgroupRepository appgrouprepo;
	
	@Autowired
	ResourceService ResSer;

	@Autowired
	ResourceRepository resourceRepository;

	@Autowired
	EmployeeService EmpSer;
	
	@Autowired
	EmployeeRepository employeeRepository;
	
	@Autowired
	RestrictionService RestrictionSer;
	
	@Autowired
	private AppointmentgroupService appointmentgroupService;
	
	@Autowired
	private ProcedureService procedureServie;
	
	@Autowired
	private ProcedureRepository procedureRepository;
	
	@Autowired
	private UserRepository userRepository;

	@Override
	public List<Appointment> getAll() {
		return repo.findAll();
	}

	@Override
	public List<Appointment> getAll(Bookable bookable) {
		List<Appointment> result = new ArrayList<>();
		for (Appointment appointment : this.getAll()) {
			if (appointment.getBookedCustomer().getId().equals(bookable.getId())) {
				result.add(appointment);
			}
			for (Employee employee : appointment.getBookedEmployees()) {
				if (employee.getId().equals(bookable.getId())) {
					result.add(appointment);
				}
			}
			for (Resource resource : appointment.getBookedResources()) {
				if (resource.getId().equals(bookable.getId())) {
					result.add(appointment);
				}
			}
		}
		return result;
	}

	/**
	 * @author Robert Schulz
	 */
	@Override
	public Appointment getById(String id) {
		Optional<Appointment> appointment = repo.findById(id);
		return appointment.orElse(null);
	}

	/**
	 * @author Robert Schulz
	 */
	@Override
	public Appointment create(Appointment newInstance) {
		if (newInstance.getId() == null) { return repo.save(newInstance); }
		if (repo.findById(newInstance.getId()).isPresent()) {
			throw new ResourceNotFoundException("Instance with the given id (" + newInstance.getId()
					+ ") exists on the database. Use the update method.");
		}
		return null;
	}

	/**
	 * @author Robert Schulz
	 */
	@Override
	public Appointment update(Appointment updatedInstance) {
		if (updatedInstance.getId() != null && repo.findById(updatedInstance.getId()).isPresent()) {
			return repo.save(updatedInstance);
		}
		return null;
	}

	@Override
	public boolean setCustomerIsWaiting(String id, boolean customerIsWaiting) {
		Appointment appointment = this.getById(id);

		if (appointment.getActualStarttime() != null && appointment.getActualEndtime() != null
				&& appointment.getStatus() != AppointmentStatus.PLANNED) {
			throw new IllegalArgumentException("Customer of this appointment can not be set");
		}

		if (!StatusService.isUpdateable(appointment.getBookedCustomer().getSystemStatus())) {
			throw new IllegalArgumentException("Customer can not be updated");
		}

		appointment.setCustomerIsWaiting(customerIsWaiting);
		appointment = repo.save(appointment);

		appointmentgroupService.setPullableAppointment(appointment);

		return appointment.isCustomerIsWaiting() == customerIsWaiting;
	}

	@Override
	public boolean delete(String id) {
		Appointment appointment = getById(id);

		appointment.setStatus(AppointmentStatus.DELETED);

		repo.save(appointment);

		return getById(id).getStatus().isDeleted();
	}

	/** author Amar Alkhankan  **/
	public Appointmentgroup getAvailableResourcesAndEmployees(Appointmentgroup group) {
		List<Employee> Employees = new ArrayList<>();
		List<Resource> Resources = new ArrayList<>();
		// get all appointments from Appointmentgroup
		List<Appointment> appointments = group.getAppointments();

		// Test
		// 1.Procedure Relations
		// 2.Appointment Times for each Appointment in appointments-List
		group.testProcedureRelations(false);
		List<TimeInterval> timelist = new ArrayList<>();
		try {
			appointments.forEach(app -> new NormalAppointmentTester(app).testAppointmentTimes(timelist));
		} catch (RuntimeException ex) {

		}
		// for each appointment
		// 1-get Booked Procedure
		// 2-get Needed ResourceTypes
		// 3-get Needed Employee Positions
		for (Appointment appointment : appointments) {
			Procedure procedureOfAppointment = procedureServie.getById(appointment.getBookedProcedure().getId());
			List<ResourceType> ResourceTypes = procedureOfAppointment.getNeededResourceTypes();
			List<Position> Positions = procedureOfAppointment.getNeededEmployeePositions();
			// for each ResourceType
			// get all resources from this Type and for each one check if:
			// Resource has any other appointment/s in the Timeinterval
			// if no appointment/s could be found then add the resource to the
			// "Resouces"-List

			if (ResourceTypes.size() > 0)
				for (ResourceType rt : ResourceTypes) {
					// boolean Resourcefound = false;
					for (Resource resource : ResSer.getResourcesbyResourceTypeandStatus(rt.getId(), Status.ACTIVE)) {
						List<Appointment> ResApps = this.getAppointmentsOfBookedResourceInTimeinterval(resource.getId(),
								appointment.getPlannedStarttime(), appointment.getPlannedEndtime(), AppointmentStatus.PLANNED);
						boolean containedinResources = Resources.stream()
								.anyMatch(res -> res.getId().equals(resource.getId()));
						boolean ResIsAva = ResSer.isResourceAvailableBetween(resource.getId(),
								appointment.getPlannedStarttime(), appointment.getPlannedEndtime());

						if (ResApps.size() == 0 && !(containedinResources) && (ResIsAva)) {
							Resources.add(resource);
							// Resourcefound = true;
							break;
						}
					}
//				if (!(Resourcefound))
//					throw new AppointmentTimeException("No resource from type" + rt.getName() + " is available for appointment with id: "+ appointment.getId(),
//							appointment);
				}
			// for each position
			// get all Employees who has this Position and for each one check if:
			// Employee has any other appointment/s in the Timeinterval
			// if no appointment/s could be found then add the employee to the
			// "Employees"-List
			if (Positions.size() > 0)
				for (Position pos : Positions) {
					// boolean Employeefound = false;
					for (Employee employee : EmpSer.getEmployeesByPositionIdandStatus(pos.getId(), Status.ACTIVE)) {
						List<Appointment> EmpApps = this.getAppointmentsOfBookedEmployeeInTimeinterval(employee.getId(),
								appointment.getPlannedStarttime(), appointment.getActualEndtime(), AppointmentStatus.PLANNED);
						boolean containedinEmployees = Employees.stream()
								.anyMatch(emp -> emp.getId().equals(employee.getId()));
						boolean EmpIsAva = EmpSer.isEmployeeAvailableBetween(employee.getId(),
								appointment.getPlannedStarttime(), appointment.getPlannedEndtime());
						if (EmpApps.size() == 0 && !(containedinEmployees) && (EmpIsAva)) {
							Employees.add(employee);
							// Employeefound = true;
							break;
						}
					}
//				if (!(Employeefound))
//					throw new AppointmentTimeException("no employee from position" + pos.getName() + " is available for appointment with id: "+ appointment.getId(),
//							appointment);
				}

			// set Resources and Employees to the appointment
			appointment.setBookedResources(Resources);
			appointment.setBookedEmployees(Employees);
			Employees = new ArrayList<>();
			Resources = new ArrayList<>();
			appointment.setBookedProcedure(procedureOfAppointment);
			// test Availabilities Of (Procedur/Employees/Resources)
			AppointmentTester tester = new NormalAppointmentTester();
			try {
				tester.testAvailabilities();
			} catch (Exception ex) {}
			// throw exceptions if not all needed employees/resources could be found
			if (Positions.size() != appointment.getBookedEmployees().size())
				throw new AppointmentTimeException(
						"In the time interval not all needed employees for appointment could be found", appointment);
			if (ResourceTypes.size() != appointment.getBookedResources().size())
				throw new AppointmentTimeException(
						"In the time interval not all needed resources for appointment could be found", appointment);
		}
		// appgrouprepo.save(group);
		return group;
	}

	// Everything below @author Joshua Müller
	@Override
	public List<Appointment> getAllAppointmentsByUseridAndWarnings(String userid, List<Warning> warnings) {
		if (warnings == null || warnings.size() == 0)
			throw new IllegalArgumentException("Warnings are needed!");

		boolean isUseridEmpty = userid == null || userid.equals("");
		boolean hasOneWarning = warnings.size() == 1;

		if (isUseridEmpty) {
			return hasOneWarning 
					? getAppointmentsByWarning(warnings.get(0)) 
							: getAppointmentsByWarnings(warnings);
		} else {
			return hasOneWarning 
					? getAppointmentsByWarningAndId(userid, warnings.get(0))
					: getAppointmentsByWarningsAndId(userid, warnings);
		}
	}

	@Override
	public List<Appointment> getAppointmentsByUserIdAndAppointmentStatus(String userid, AppointmentStatus status) {
		return status == null 
				? getAppointmentsByUserid(userid) 
				: getAppointmentsByUserid(userid, status);
	}

	@Override
	@PreAuthorize("hasAuthority('APPOINTMENT_READ')")
	public List<Appointment> getAppointmentsByEmployeeIdAndAppointmentStatus(String employeeid,
			AppointmentStatus status) {
		return status == null 
				? getAppointmentsByEmployeeid(employeeid) 
				: getAppointmentsByEmployeeid(employeeid, status);
	}

	@Override
	@PreAuthorize("hasAuthority('APPOINTMENT_READ')")
	public List<Appointment> getAppointmentsByResourceIdAndAppointmentStatus(String resourceid,
			AppointmentStatus status) {
		return status == null 
				? getAppointmentsByResourceid(resourceid) 
				: getAppointmentsByResourceid(resourceid, status);
	}

	@Override
//	@PreAuthorize("hasAuthority('APPOINTMENT_READ')")
	public List<Appointment> getAppointmentsByProcedureIdAndAppointmentStatus(String procedureid,
			AppointmentStatus status) {
		return status == null 
				? repo.findByBookedProcedureId(procedureid)
				: repo.findByBookedProcedureIdAndStatus(procedureid, status);
	}

	@Override
	public List<Appointment> getAppointmentsOfBookedEmployeeInTimeinterval(String employeeid, Date starttime,
			Date endtime, AppointmentStatus status) {
		return status == null 
				? repo.findAppointmentsByBookedEmployeeInTimeinterval(employeeid, starttime, endtime)
				: repo.findAppointmentsByBookedEmployeeInTimeintervalWithStatus(employeeid, starttime, endtime, status);
	}

	@Override
	@PreAuthorize("hasAuthority('APPOINTMENT_READ')")
	public List<Appointment> getAppointmentsOfBookedResourceInTimeinterval(String resourceid, Date starttime,
			Date endtime, AppointmentStatus status) {
		return status == null 
				? repo.findAppointmentsByBookedResourceInTimeinterval(resourceid, starttime, endtime)
				: repo.findAppointmentsByBookedResourceInTimeintervalWithStatus(resourceid, starttime, endtime, status);
	}

	@Override
	public List<Appointment> getAppointmentsOfBookedCustomerInTimeinterval(String userid, Date starttime, Date endtime,
			AppointmentStatus status) {
		return status == null 
				? repo.findAppointmentsByBookedCustomerInTimeinterval(userid, starttime, endtime)
				: repo.findAppointmentsByBookedCustomerInTimeintervalWithStatus(userid, starttime, endtime, status);
	}

	@Override
	@PreAuthorize("hasAuthority('APPOINTMENT_READ')")
	public List<Appointment> getAppointmentsOfBookedProcedureInTimeinterval(String procedureid, Date starttime,
			Date endtime, AppointmentStatus status) {
		return repo.findAppointmentsByBookedProceudreInTimeintervalWithStatus(procedureid, starttime, endtime, status);
	}

	@Override
	@PreAuthorize("hasAuthority('APPOINTMENT_READ')")
	public List<Appointment> getAppointmentsWithUseridAndTimeInterval(String userid, Date starttime, Date endtime) {
		return repo.findAppointmentsByBookedUserAndTimeinterval(userid, starttime, endtime);
	}

	@Override
	@PreAuthorize("hasAuthority('APPOINTMENT_READ')")
	public List<Appointment> getAppointmentsWithCustomerEmployeeResourceAfterDate(List<ObjectId> employeeids,
			List<ObjectId> resourceids, Date starttime, AppointmentStatus status) {
		return repo.findAppointmentsWithCustomerEmployeeAndResourceAfterPlannedStarttime(employeeids, resourceids,
				starttime, status);
	}

	@Override
	@PreAuthorize("hasAuthority('APPOINTMENT_READ')")
	public List<Appointment> getAppointmentsInTimeIntervalAndStatus(Date starttime, Date endtime,
			AppointmentStatus status) {
		return status == null 
				? getAppointmentsInTimeInterval(starttime, endtime)
				: getAppointmentsInTimeIntervalWithStatus(starttime, endtime, status);
	}

	@Override
	@PreAuthorize("hasAuthority('APPOINTMENT_READ')")
	public List<Appointment> getOverlappingAppointmentsInTimeInterval(Date starttime, Date endtime,
			AppointmentStatus status) {
		return repo.findAllOverlappingAppointmentsWithStatus(status, starttime, endtime);
	}

	@Override
	@PreAuthorize("hasAuthority('APPOINTMENT_READ')")
	public List<Appointment> getAppointments(Available available, Date startdate) {
		return available.getAppointmentsAfterDate(this, startdate);
	}

	@Override
	@PreAuthorize("hasAuthority('APPOINTMENT_READ')")
	public List<Appointment> getAppointmentsOf(Employee employee, Date startdate) {
		return repo.findByBookedEmployeesIdAndPlannedStarttimeAfter(employee.getId(), startdate);
	}

	@Override
	@PreAuthorize("hasAuthority('APPOINTMENT_READ')")
	public List<Appointment> getAppointmentsOf(Procedure procedure, Date startdate) {
		return repo.findByBookedProcedureIdAndPlannedStarttimeAfter(procedure.getId(), startdate);
	}

	@Override
	@PreAuthorize("hasAuthority('APPOINTMENT_READ')")
	public List<Appointment> getAppointmentsOf(Resource resource, Date startdate) {
		return repo.findByBookedResourcesIdAndPlannedStarttimeAfter(resource.getId(), startdate);
	}

	private List<Appointment> getAppointmentsByWarning(Warning warning) {
		return repo.findByWarnings(warning);
	}

	private List<Appointment> getAppointmentsByWarnings(List<Warning> warnings) {
		return repo.findByWarningsIn(warnings);
	}

	private List<Appointment> getAppointmentsByWarningAndId(String userid, Warning warning) {
		return repo.findByBookedCustomerIdAndWarnings(userid, warning);
	}

	private List<Appointment> getAppointmentsByWarningsAndId(String userid, List<Warning> warnings) {
		return repo.findByBookedCustomerIdAndWarningsIn(userid, warnings);
	}

	private List<Appointment> getAppointmentsByUserid(String id) {
		return repo.findByBookedCustomerId(id);
	}

	private List<Appointment> getAppointmentsByUserid(String id, AppointmentStatus appointmentStatus) {
		return repo.findByBookedCustomerIdAndStatus(id, appointmentStatus);
	}

	private List<Appointment> getAppointmentsByEmployeeid(String id) {
		return repo.findByBookedEmployeesId(id);
	}

	private List<Appointment> getAppointmentsByEmployeeid(String employeeid, AppointmentStatus appointmentStatus) {
		return repo.findByBookedEmployeesIdAndStatus(employeeid, appointmentStatus);
	}

	private List<Appointment> getAppointmentsByResourceid(String id) {
		return repo.findByBookedResourcesId(id);
	}

	private List<Appointment> getAppointmentsByResourceid(String resourceid, AppointmentStatus appointmentStatus) {
		return repo.findByBookedResourcesIdAndStatus(resourceid, appointmentStatus);
	}

	private List<Appointment> getAppointmentsInTimeIntervalWithStatus(Date starttime, Date endtime,
			AppointmentStatus status) {
		return repo.findAppointmentsByTimeintervalAndStatus(status, starttime, endtime);
	}

	private List<Appointment> getAppointmentsInTimeInterval(Date starttime, Date endtime) {
		return repo.findAppointmentsByTimeinterval(starttime, endtime);
	}

	public List<Appointment> cleanseAppointmentsOfBlocker(List<Appointment> appointments) {
		return appointments.stream()
						.filter(appointment -> appointment.getBookedProcedure() != null)
						.collect(Collectors.toList());
	}

	public void loadAppointment(Appointment appointmentToLoad) {
		Procedure bookedProcedure = appointmentToLoad.getBookedProcedure();
		User bookedCustomer = appointmentToLoad.getBookedCustomer();
		
		List<Employee> employees = new ArrayList<>();
		List<Resource> resources = new ArrayList<>();
		Procedure procedure = procedureRepository.findById(bookedProcedure.getId()).orElseThrow();
		User customer = bookedCustomer.getId() != null 
										? userRepository.findById(bookedCustomer.getId()).orElseThrow() 
										: null;

		// populate list of employees
		appointmentToLoad.getBookedEmployees().stream()
						.filter(employee -> employee.getId() != null)
						.forEach(employee -> employees.add(
								employeeRepository.findById(employee.getId())
										.orElseThrow()));
		// populate list of resources
		appointmentToLoad.getBookedResources().stream()
						.filter(resource -> resource.getId() != null)
						.forEach(resource -> resources.add(
								resourceRepository.findById(resource.getId())
										.orElseThrow()));

		appointmentToLoad.setBookedEmployees(employees);
		appointmentToLoad.setBookedResources(resources);
		appointmentToLoad.setBookedProcedure(procedure);
		appointmentToLoad.setBookedCustomer(customer);
		
		appointmentToLoad.setStatus(AppointmentStatus.PLANNED);

		if (appointmentToLoad.notStartedOrEnded())
			throw new ProcedureException("Appointment of an procedure contains actual times", procedure);
	}
}

package com.dvproject.vertTerm.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dvproject.vertTerm.Model.Appointment;
import com.dvproject.vertTerm.Model.AppointmentStatus;
import com.dvproject.vertTerm.Model.Blocker;
import com.dvproject.vertTerm.Model.BookingTester;
import com.dvproject.vertTerm.Model.Employee;
import com.dvproject.vertTerm.Model.NormalBookingTester;
import com.dvproject.vertTerm.Model.OverrideBookingTester;
import com.dvproject.vertTerm.Model.Resource;
import com.dvproject.vertTerm.Model.TimeInterval;
import com.dvproject.vertTerm.Model.Warning;
import com.dvproject.vertTerm.exception.AppointmentException;
import com.dvproject.vertTerm.exception.AppointmentTimeException;
import com.dvproject.vertTerm.repository.AppointmentRepository;
import com.dvproject.vertTerm.repository.BlockerRepository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/** author Amar Alkhankan **/
@Service
@Transactional
public class BlockerServiceImp implements BlockerService {

	@Autowired
	private BlockerRepository blockerRepo;
	@Autowired
	private AppointmentRepository appointmentRepo;

	@Autowired
	private AppointmentService appointmentService;
	@Autowired
	private AppointmentServiceImpl appointmentServiceImp;
	

	// @PreAuthorize("hasAuthority('')")
	public Blocker create(Blocker blocker) {
		// create new blocker 
		
		// Test blocker Start-Time and End-Time 
		if (blocker.getPlannedStarttime().after(blocker.getPlannedEndtime()))
			throw new ResourceNotFoundException("The planned starttime is after the planned endtime");
		if (blocker.getPlannedStarttime().before(Date.from(Instant.now())))
			throw new ResourceNotFoundException("The planned starttime is in the past");	
		
		blockerRepo.save(blocker);
		SetOrRemoveWarningFlag(blocker.getId(), "SET");
		return blocker;

	}

	public void SetOrRemoveWarningFlag(String id, String SetorRemove) {
		// get Blocker by ID from DB
		Optional<Blocker> BlockerDb = this.blockerRepo.findById(id);

		// if Blocker exists
		if (BlockerDb.isPresent()) {
			Blocker blocker = BlockerDb.get();
			List<Appointment> appointments = new ArrayList<>();
		
			// for each employee in Blocker's BookedEmployees List
			// get all appointments from the employee in the time interval of the blocker
			// and add them to the appointments-List
			if (blocker.getBookedEmployees() != null)
				for (Employee emp : blocker.getBookedEmployees()) {
					List<Appointment> EmpApps = appointmentService.getAppointmentsOfBookedEmployeeInTimeinterval(
							emp.getId(), blocker.getPlannedStarttime(), blocker.getPlannedEndtime(),AppointmentStatus.PLANNED);

					appointments.addAll(EmpApps);
				}
			// for each resource in Blocker's BookedResources List
			// get all appointments from the resource in the time interval of the blocker
			// and add them to the appointments-List
			if (blocker.getBookedResources() != null)
				for (Resource res : blocker.getBookedResources()) {
					List<Appointment> ResApps = appointmentService.getAppointmentsOfBookedResourceInTimeinterval(
							res.getId(), blocker.getPlannedStarttime(), blocker.getPlannedEndtime(),AppointmentStatus.PLANNED);

					appointments.addAll(ResApps);
				}

//			// get all appointments from DB in the time interval of the blocker
//			if(appointments.size() == 0 )
//				appointments= appointmentService.getAppointmentsInTimeIntervalAndStatus(blocker.getPlannedStarttime(), blocker.getPlannedEndtime(), AppointmentStatus.PLANNED);
//			

			// if the appointments-List not empty
			// for each appointment in appointments-List SET/REMOVE
			// (APPOINTMENT_WARNING)-flag
			if (appointments != null && appointments.size() > 0) {
				appointments=appointmentService.cleanseAppointmentsOfBlocker(appointments);
				if (SetorRemove.equals("SET"))
					SetWarning(appointments);
				else
					RemoveWarning(appointments);
			}
//			else
//				throw new ResourceNotFoundException("There are no Appointments");

		} else {
			throw new ResourceNotFoundException("Blocker with the given id :" + id + " not found");

		}
	}

	public void SetWarning(List<Appointment> appointments) {
		// set "APPOINTMENT_WARNING" in all appointments and save changed to DB
		for (Appointment app : appointments) {
			// Appointment appDB = this.appointmentService.getById(app.getId());
			if (app.addWarning(Warning.APPOINTMENT_WARNING))
				appointmentRepo.save(app);
//			else
			// throw new AppointmentException("Appointment has already 'APPOINTMENT_WARNING'
			// ", app);
		}
	}

	public void RemoveWarning(List<Appointment> appointments) {
		BookingTester tester = new OverrideBookingTester();
		// test appointment , remove "AppointmentWarning" Flag and
		// save changed to DB
		for (Appointment app : appointments) {
			// Appointment appDB = this.appointmentService.getById(app.getId());			
			app.removeWarning(Warning.APPOINTMENT_WARNING);
			tester.setAppointment(app);
			tester.testAppointment(appointmentServiceImp);
		
		}

	}

	// @PreAuthorize("hasAuthority('')")
	public Blocker update(Blocker blocker) {
		// update a blocker if it's exist
		// blocker.setName(capitalize(blocker.getName()));
		if (blockerRepo.findById(blocker.getId()).isPresent()) {
			// remove "APPOINTMENT_WARNING" from all appointments in the old blocker
			SetOrRemoveWarningFlag(blocker.getId(), "REMOVE");
			// add "APPOINTMENT_WARNING" to all appointments in the new blocker
			Blocker NewBlocker = blockerRepo.save(blocker);
			SetOrRemoveWarningFlag(NewBlocker.getId(), "SET");
			return NewBlocker;
		} else {
			throw new ResourceNotFoundException("Blocker with the given id :" + blocker.getId() + "not found");
		}
	}

	// @PreAuthorize("hasAuthority('')")
	public List<Blocker> getAll() {
		// get all blockers from DB
		return this.blockerRepo.findAll();

	}

	// @PreAuthorize("hasAuthority('')")
	public Blocker getById(String id) {
		// get a blocker by the blocker-ID
		Optional<Blocker> BlockerDb = this.blockerRepo.findById(id);
		if (BlockerDb.isPresent()) {
			return BlockerDb.get();
		} else {
			throw new ResourceNotFoundException("Blocker with the given id :" + id + " not found");
		}
	}

	/**
	 * @author Joshua Müller
	 */
	@Override
	@PreAuthorize("hasAuthority('APPOINTMENT_READ')")
	public boolean exists(String id) {
		return blockerRepo.findById(id).isPresent();
	}

	// @PreAuthorize("hasAuthority('')")
	public List<Blocker> getBlockers(String[] ids) {
		// get all blockers if their ID exists in the given ids-list
		List<Blocker> Blockers = new ArrayList<>();
		for (String id : ids) {
			Blockers.add(this.getById(id));
		}

		return Blockers;
	}

	// @PreAuthorize("hasAuthority('')")
	public boolean delete(String id) {
		// change blocker_Status to 'DELETED'
		Blocker blocker;
		if (blockerRepo.findById(id).isPresent()) {
			blocker = getById(id);
			blocker.setStatus(AppointmentStatus.DELETED);
			blockerRepo.save(blocker);
			SetOrRemoveWarningFlag(id, "REMOVE");

		} else {
			throw new ResourceNotFoundException("Blocker with the given id : " + id + " not found ");
		}
		return blocker.getStatus() == AppointmentStatus.DELETED;
	}

	// capitalize first letter of a string
	public static String capitalize(String str) {
		if (str == null)
			return str;
		return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();

	}

}

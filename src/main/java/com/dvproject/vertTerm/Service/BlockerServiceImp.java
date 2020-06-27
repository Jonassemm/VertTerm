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
import com.dvproject.vertTerm.Model.Resource;
import com.dvproject.vertTerm.Model.Warning;
import com.dvproject.vertTerm.exception.AppointmentException;
import com.dvproject.vertTerm.exception.AppointmentTimeException;
import com.dvproject.vertTerm.repository.AppointmentRepository;
import com.dvproject.vertTerm.repository.BlockerRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
	
	//@PreAuthorize("hasAuthority('')")
	public Blocker create(Blocker blocker) {
		//create new blocker if not exist
		blocker.setName(capitalize(blocker.getName()));
		if (this.blockerRepo.findByname(blocker.getName()) == null) {			
			blockerRepo.save(blocker);	
			SetOrRemoveWarningFlag(blocker.getId(), "SET");
				return blocker;
				
		} else {
			throw new ResourceNotFoundException("Blocker with the given name :" + blocker.getName() + " already exsist");
		}

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
			for (Employee emp : blocker.getBookedEmployees()) {
				List<Appointment> EmpApps = emp.getAppointmentsOfBookedEmployeeInTimeinterval(appointmentService, 					emp.getId(),blocker.getPlannedStarttime(), blocker.getPlannedEndtime());
				
				appointments.addAll(EmpApps);
			}
			// for each resource in Blocker's BookedResources List
			// get all appointments from the resource in the time interval of the blocker
			// and add them to the appointments-List
			for (Resource res : blocker.getBookedResources()) {
				List<Appointment> ResApps = res.getAppointmentsOfBookedResourceInTimeinterval(appointmentService, res.getId(),
						blocker.getPlannedStarttime(), blocker.getPlannedEndtime());
				
				appointments.addAll(ResApps);
			}
			// if the appointments-List not empty
			// for each appointment in appointments-List SET/REMOVE a warning flag
			if (appointments != null && appointments.size() > 0) {
				if (SetorRemove.equals("SET"))
					SetWarning(appointments);
				else
					RemoveWarning(appointments);
			} else
				throw new ResourceNotFoundException("There are no Appointments");

		} else {
			throw new ResourceNotFoundException("Blocker with the given id :" + id + " not found");
			
		}
	}

	public void SetWarning(List<Appointment> appointments) {
		// set "AppointmentWarning" in all appointments and save changed to DB
		for (Appointment app : appointments) {
			Appointment appDB = this.appointmentService.getById(app.getId());
			if (appDB.addWarning(Warning.APPOINTMENT_WARNING))
				appointmentRepo.save(appDB);
			//else
			//	throw new AppointmentException("Appointment has already 'APPOINTMENT_WARNING' ", app);
		}

	}

	public void RemoveWarning(List<Appointment> appointments) {
		BookingTester tester = new NormalBookingTester();
		//test every appointment remove "AppointmentWarning" from all appointments and save changed to DB
			for (Appointment app : appointments) {
				Appointment appDB = this.appointmentService.getById(app.getId());
				if (appDB.removeWarning(Warning.APPOINTMENT_WARNING))
				{	
				  appointmentRepo.save(appDB);
				 try {
	                tester.testAppointment(appointmentServiceImp);
	            } catch(Exception ex) {
					// setze Warning
	            	appDB.addWarning(Warning.APPOINTMENT_WARNING);
				}
				}
			//	else
			//		throw new AppointmentException("Appointment "+ app.getId()+" has no 'APPOINTMENT_WARNING' to 					remove it", app);
			}
		
	}
  
	//@PreAuthorize("hasAuthority('')")
	//work on it 
	public Blocker update(Blocker blocker) {
		//update a blocker if it's exist
		if (blockerRepo.findById(blocker.getId()).isPresent()) {
			SetOrRemoveWarningFlag(blocker.getId(), "REMOVE");
			blocker.setName(capitalize(blocker.getName()));
			Blocker b = blockerRepo.save(blocker);
			SetOrRemoveWarningFlag(blocker.getId(), "SET");
			return b;
		} else {
			throw new ResourceNotFoundException("Blocker with the given id :" + blocker.getId() + "not found");
		}
	}

	//@PreAuthorize("hasAuthority('')")
	public List<Blocker> getAll() {
		//get all blockers from DB
		return this.blockerRepo.findAll();

	}

	//@PreAuthorize("hasAuthority('')")
	public Blocker getById(String id) {
		//get a blocker by the blocker-ID
		Optional<Blocker> BlockerDb = this.blockerRepo.findById(id);
		if (BlockerDb.isPresent()) {
			return BlockerDb.get();
		} else {
			throw new ResourceNotFoundException("Blocker with the given id :" + id + " not found");
		}
	}

	//@PreAuthorize("hasAuthority('')")
	public List<Blocker> getBlockers(String[] ids) {
		//get all blockers if their ID exists in the given ids-list
		List<Blocker> Blockers = new ArrayList<>();
		for (String id : ids) {
			Blockers.add(this.getById(id));
		}

		return Blockers;
	}

	//@PreAuthorize("hasAuthority('')")
	public boolean delete(String id) {
		//change blocker_Status to 'DELETED'
		if (blockerRepo.findById(id).isPresent()) {
			Blocker blocker = getById(id);
			SetOrRemoveWarningFlag(id, "REMOVE");
			blocker.setStatus(AppointmentStatus.DELETED);
			blockerRepo.save(blocker);

		} else {
			throw new ResourceNotFoundException("Blocker with the given id : " + id + " not found ");
		}
		Blocker blocker = getById(id);
		blocker.setStatus(AppointmentStatus.DELETED);
		blockerRepo.save(blocker);
		return blocker.getStatus() == AppointmentStatus.DELETED;
	}
	
	//capitalize first letter of a string 
	public static String capitalize(String str) {
		if (str == null)
			return str;
		return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();

	}

}

package com.dvproject.vertTerm.util;

import org.springframework.security.core.context.SecurityContextHolder;

import com.dvproject.vertTerm.Model.Appointmentgroup;
import com.dvproject.vertTerm.Model.User;
import com.dvproject.vertTerm.Service.AppointmentService;
import com.dvproject.vertTerm.Service.RestrictionService;
import com.dvproject.vertTerm.repository.UserRepository;

/**
 * @author Joshua Müller
 */
public abstract class Booker {
	protected Appointmentgroup appointmentgroupToBook;
	private org.springframework.security.core.userdetails.User logedInUser;

	public Booker() {
		logedInUser = (org.springframework.security.core.userdetails.User) 
				SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	}
	
	public Booker (Appointmentgroup appointmentgroupToBook) {
		this();
		this.appointmentgroupToBook = appointmentgroupToBook;
	}
	
	public void setAppointmentgroup(Appointmentgroup appointmentgroup){
		this.appointmentgroupToBook = appointmentgroup;
	}
	
	public void bookable(AppointmentService appointmentService,
			RestrictionService restrictionService, UserRepository userRepository) {
		User booker = userRepository.findByUsername(logedInUser.getUsername());
		
		appointmentgroupToBook.resetAllWarnings();
		
		if (!appointmentgroupToBook.hasDistinctProcedures())
			throw new IllegalArgumentException("Appointments contain duplicate procedures or procedures with id == null");
		
		testAppointmentgroup(appointmentService, restrictionService, booker);
	}

	protected abstract void testAppointmentgroup(AppointmentService appointmentService,
			RestrictionService restrictionService, User booker);
	
	public abstract void testProcedureRelations();
}

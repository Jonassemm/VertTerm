package com.dvproject.vertTerm.Model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;

public class Appointment implements Serializable {
	private static final long serialVersionUID = 2862268218236152790L;
	
	@Id
	private String id;
	private String description;
	@NotNull
	private AppointmentStatus status;
	
	private Date plannedStarttime;
	private Date plannedEndtime;
	private Date actualStarttime;
	private Date actualEndtime;
	
	@DBRef
	private Procedure procedure;
	@DBRef
	@NotEmpty
	private List<User> bookedCustomers;
	@DBRef
	private List<Employee> bookedEmployees;
	@DBRef
	private List<Resource> bookedResources;
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public AppointmentStatus getStatus() {
		return status;
	}
	
	public void setStatus(AppointmentStatus status) {
		this.status = status;
	}
	
	public Date getPlannedEndtime() {
		return plannedEndtime;
	}
	
	public void setPlannedEndtime(Date plannedEndtime) {
		this.plannedEndtime = plannedEndtime;
	}
	
	public Date getPlannedStarttime() {
		return plannedStarttime;
	}
	
	public void setPlannedStarttime(Date plannedStarttime) {
		this.plannedStarttime = plannedStarttime;
	}
	
	public Date getActualStarttime() {
		return actualStarttime;
	}
	
	public void setActualStarttime(Date actualStarttime) {
		this.actualStarttime = actualStarttime;
	}
	
	public Date getActualEndtime() {
		return actualEndtime;
	}
	
	public void setActualEndtime(Date actualEndtime) {
		this.actualEndtime = actualEndtime;
	}
	
	public Procedure getProcedure() {
		return procedure;
	}
	
	public void setProcedure(Procedure procedure) {
		this.procedure = procedure;
	}
	
	public List<User> getBookedCustomers() {
		return bookedCustomers;
	}
	
	public void setBookedCustomers(List<User> bookedCustomers) {
		this.bookedCustomers = bookedCustomers;
	}
	
	public List<Employee> getBookedEmployees() {
		return bookedEmployees;
	}
	
	public void setBookedEmployees(List<Employee> bookedEmployees) {
		this.bookedEmployees = bookedEmployees;
	}
	
	public List<Resource> getBookedResources() {
		return bookedResources;
	}
	
	public void setBookedResources(List<Resource> bookedResources) {
		this.bookedResources = bookedResources;
	}

}

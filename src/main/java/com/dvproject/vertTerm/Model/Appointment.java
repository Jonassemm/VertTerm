package com.dvproject.vertTerm.Model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;

public class Appointment implements Serializable {
	private static final long serialVersionUID = 2862268218236152790L;
	
	@Id
	private String id;
	@Indexed(unique = true)
	private String name;
	private String description;
	@NotNull
	private AppointmentStatus status;
	
	private Date targetStartTime;
	private Date targetEndTime;
	private Date actualStartTime;
	private Date actualEndTime;
	
	private Date timeOfCreation;
	private Date timeOfBooking;
	
	@DBRef
	private Procedure procedure;
	@DBRef
	@NotEmpty
	private List<Customer> customers;
	@DBRef
	@NotEmpty
	private List<Employee> employees;
	@DBRef
	@NotEmpty
	private List<Resource> resources;
	
	public Appointment () {
		timeOfCreation = new Date();
	}
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
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
	
	public Date getTargetStartTime() {
		return targetStartTime;
	}
	
	public void setTargetStartTime(Date targetStartTime) {
		this.targetStartTime = targetStartTime;
	}
	
	public Date getTargetEndTime() {
		return targetEndTime;
	}
	
	public void setTargetEndTime(Date targetEndTime) {
		this.targetEndTime = targetEndTime;
	}
	
	public Date getActualStartTime() {
		return actualStartTime;
	}
	
	public void setActualStartTime(Date actualStartTime) {
		this.actualStartTime = actualStartTime;
	}
	
	public Date getActualEndTime() {
		return actualEndTime;
	}
	
	public void setActualEndTime(Date actualEndTime) {
		this.actualEndTime = actualEndTime;
	}
	
	public Date getTimeOfCreation() {
		return timeOfCreation;
	}
	
	public void setTimeOfCreation(Date timeOfCreation) {
		this.timeOfCreation = timeOfCreation;
	}
	
	public Date getTimeOfBooking() {
		return timeOfBooking;
	}
	
	public void setTimeOfBooking(Date timeOfBooking) {
		this.timeOfBooking = timeOfBooking;
	}
	
	public Procedure getProcedure() {
		return procedure;
	}
	
	public void setProcedure(Procedure procedure) {
		this.procedure = procedure;
	}
	
	public List<Customer> getCustomers() {
		return customers;
	}
	
	public void setCustomers(List<Customer> customers) {
		this.customers = customers;
	}
	
	public List<Employee> getEmployees() {
		return employees;
	}
	
	public void setEmployees(List<Employee> employees) {
		this.employees = employees;
	}
	
	public List<Resource> getResources() {
		return resources;
	}
	
	public void setResources(List<Resource> resources) {
		this.resources = resources;
	}

}

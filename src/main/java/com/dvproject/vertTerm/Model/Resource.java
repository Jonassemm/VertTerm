package com.dvproject.vertTerm.Model;

import java.io.Serializable;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import com.dvproject.vertTerm.Service.AppointmentService;
import com.dvproject.vertTerm.exception.AvailabilityException;

@Document("resource")
public class Resource extends Bookable implements Serializable, Available {
	private static final long serialVersionUID = 7443614129275947603L;

	@Indexed(unique = true)
	private String name;
	private String description;
	@NotNull
	private Status status;
	@DBRef
	private List<Resource> childResources;
	@DBRef
	private List<Restriction> restrictions;
	@DBRef
	private List<ResourceType> resourceTypes;

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

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public List<Resource> getChildResources() {
		return childResources;
	}

	public void setChildResources(List<Resource> childResources) {
		this.childResources = childResources;
	}

	public List<Restriction> getRestrictions() {
		return restrictions;
	}

	public void setRestrictions(List<Restriction> restrictions) {
		this.restrictions = restrictions;
	}

	public List<ResourceType> getResourceTypes() {
		return resourceTypes;
	}

    public void setResourceTypes(List<ResourceType> resourceTypes) {
        this.resourceTypes = resourceTypes;
    }
    
    public void isAvailable(Date startdate, Date enddate) {
		for (Availability availability : super.getAvailabilities()) {
			if (availability.isAvailableBetween(startdate, enddate)) {
				return;
			}
		}
		
		throw new AvailabilityException("No availability for the resource " + name);
    }
    
	@Override
	public List<Appointment> getAppointmentsOfAvailable(AppointmentService appointmentService, Date endOfSeries) {
		return appointmentService.getAppointmentsOfResource(super.getId(), endOfSeries);
	}
}

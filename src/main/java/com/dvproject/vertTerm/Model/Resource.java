package com.dvproject.vertTerm.Model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotNull;

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
		if (!getAvailabilities().stream().anyMatch(availability -> availability.isAvailableBetween(startdate, enddate)))
			throw new AvailabilityException("No availability for the resource " + name);
	}

	

	@Override
	public List<Appointment> getAppointmentsAfterDate(AppointmentService appointmentService, Date startdate) {
		List<Appointment> appointments = appointmentService.getAppointmentsOf(this, startdate);

		for (Appointment appointment : appointments) {
			List<Resource> resources = appointment.getBookedResources();
			Resource resource;
			for (int i = 0; i < resources.size(); i++) {
				resource = resources.get(i);

				if (resource.getId().equals(getId())) {
					resources.set(i, this);
					break;
				}
			}
		}

		return appointments;
	}
}

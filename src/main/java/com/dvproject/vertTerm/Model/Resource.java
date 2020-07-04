package com.dvproject.vertTerm.Model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
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
		if (!getAvailabilities().stream().anyMatch(availability -> availability.isAvailableBetween(startdate, enddate)))
			throw new AvailabilityException("No availability for the resource " + name);
	}

	public List<Appointment> getAppointmentsOfBookedResourceInTimeinterval(AppointmentService AppoService, String id,
			Date starttime, Date endtime) {

		List<Appointment> ResApps = AppoService.getAppointmentsOfBookedResourceInTimeinterval(id, starttime, endtime,
				AppointmentStatus.PLANNED);

		if (ResApps.size() > 0 && (ResApps != null)) {
			return ResApps;
		} else {
			throw new ResourceNotFoundException("No appointments from resource with the id: " + id
					+ " in the time interval of the blocker appointment could be found");
		}

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

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

@Document("resource")
public class Resource implements Serializable{
	private static final long serialVersionUID = 7443614129275947603L;
	
	@Id
	private String id;
	@Indexed(unique = true)
	private String name;
	private String description;
	
	private List<Availability> availabilities;
	@NotNull
	private Status status;
	@DBRef
	private List<Resource> childResources;
	@DBRef
	private List<Restriction> restrictions;
	@DBRef
	private List<ResourceType> resourceTypes;

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

    public List<Availability> getAvailability() {
        return availabilities;
    }

    public void setAvailability(List<Availability> availabilities) {
        this.availabilities = availabilities;
    }

	public List<Availability> getAvailabilities() {
		return availabilities;
	}

	public void setAvailabilities(List<Availability> availabilities) {
		this.availabilities = availabilities;
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

    private Date getAvailableDateByAvailablility(Date date, Duration duration){
        Date earliestDate = null;
        for(Availability availability : this.getAvailabilities()){
            Date currentBestAvailability = availability.getEarliestAvailability(date, duration);
            if(currentBestAvailability != null){
                if(earliestDate == null){
                    earliestDate = currentBestAvailability;
                }
                else if(earliestDate.before(earliestDate)){
                    earliestDate = currentBestAvailability;
                }
            }
        }
        return earliestDate;
    }

    public boolean isAvailable(Date date, Duration duration){
        return date.equals(this.getAvailableDate(date, duration));
    }

    public Date getAvailableDate(Date date, Duration duration) {
        Date dateByAvailability = this.getAvailableDateByAvailablility(date, duration);
        if (dateByAvailability == null) {
            return null;
        }
        if (dateByAvailability.after(date)) {
            return this.getAvailableDate(dateByAvailability, duration);
        }
        return date;
    }
}

package com.dvproject.vertTerm.Model;

import java.io.Serializable;
import java.util.List;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("ressource")
public class Resource implements Serializable{
	private static final long serialVersionUID = 7443614129275947603L;
	
	@Id
	private String id;
	@Indexed(unique = true)
	private String name;
	private String description;
	
	@NotEmpty
	private List<Availability> availabilities;
	@NotNull
	private Status status;
	@DBRef
	private Resource inheritedRessource;

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

	public Resource getInheritedRessource() {
		return inheritedRessource;
	}

	public void setInheritedRessource(Resource inheritedRessource) {
		this.inheritedRessource = inheritedRessource;
	}

	public List<Availability> getAvailability() {
		return availabilities;
	}

	public void setAvailability(List<Availability> availabilities) {
		this.availabilities = availabilities;
	}
}

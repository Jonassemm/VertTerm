package com.dvproject.vertTerm.Model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author Joshua Müller
 */
@Document("position")
public class Position {
	@Id
	private String id;
	@Indexed(unique = true)
	private String name;
	private String description;

	private Status status;

	public Position () {}

	public Position (String name, String description) {
		this.setName(name);
		this.setDescription(description);
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

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public boolean sameAs(Position position) {
		return id.equals(position.getId());
	}

}

package com.dvproject.vertTerm.Model;

import java.util.List;

import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author Joshua Müller
 */
@Document
public class Role {

    @Id
    private String id;
    @Indexed(unique = true)
    private String name;
    private String description;
 
    @DBRef
    @NotNull
    private List<Right> rights;

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

	public List<Right> getRights() {
        return rights;
    }

    public void setRights(List<Right> rights) {
	    this.rights = rights;
	}   
    
}
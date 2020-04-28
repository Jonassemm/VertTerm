package com.dvproject.vertTerm.Model;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

public class User implements Serializable
{
	private static final long serialVersionUID = 1L;

	@Id
	private String id;
	@Indexed(unique=true)
	private String username;
	private String password;
	private String firstName;
	private String lastName;
	
	@DBRef
	private List<Role> role_id;

	@Override
	public String toString() {
		return "User [id=" + id + ", firstName=" + firstName
				+ ", lastName=" + lastName + "]";
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getUsername() {
	    return username;
	}

	public void setUsername(String username) {
	    this.username = username;
	}

	public String getPassword() {
	    return password;
	}

	public void setPassword(String password) {
	    this.password = password;
	}

	public List<Role> getRoles() {
	    return role_id;
	}

	public void setRoles(List<Role> role_id) {
	    this.role_id = role_id;
	}
}
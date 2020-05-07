package com.dvproject.vertTerm.Model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("user")
public class User implements Serializable
{
    private static final long serialVersionUID = -5252169753921361843L;
    	
	@Id
	private String id;
	@Indexed(unique = true)
	private String username;
	private String password;
	private String firstName;
	private String lastName;
	private Date timeOfCreation;
	private int systemStatus;
	@NotNull
	private Status status;
	
	@DBRef
	private List<Role> role_id;

	public User(String id, String username, String password, String firstName, String lastName, List<Role> role_id) {
		this();
		this.id = id;
		this.username = username;
		this.password = password;
		this.firstName = firstName;
		this.lastName = lastName;
		this.role_id = role_id;
	}

	public User() {
		timeOfCreation = new Date();
		this.status = Status.ACTIVE;
	}

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

	public Date getTimeOfCreation() {
	    return timeOfCreation;
	}

	public void setTimeOfCreation(Date timeOfCreation) {
	    this.timeOfCreation = timeOfCreation;
	}

	public int getSystemStatus() {
	    return systemStatus;
	}

	public void setSystemStatus(int systemStatus) {
	    this.systemStatus = systemStatus;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

}
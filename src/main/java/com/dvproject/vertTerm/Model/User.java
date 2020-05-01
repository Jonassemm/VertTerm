package com.dvproject.vertTerm.Model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

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
	private Timestamp timeOfCreation;
	private int systemStatus;
	
	@DBRef
	private List<Role> role_id;

	public User(String id, String username, String password, String firstName, String lastName, List<Role> role_id) {
		this.id = id;
		this.username = username;
		this.password = password;
		this.firstName = firstName;
		this.lastName = lastName;
		this.role_id = role_id;
	}

	public User() {
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

	public Timestamp getTimeOfCreation() {
	    return timeOfCreation;
	}

	public void setTimeOfCreation(Timestamp timeOfCreation) {
	    this.timeOfCreation = timeOfCreation;
	}

	public int getSystemStatus() {
	    return systemStatus;
	}

	public void setSystemStatus(int systemStatus) {
	    this.systemStatus = systemStatus;
	}
}
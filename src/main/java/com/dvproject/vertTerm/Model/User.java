package com.dvproject.vertTerm.Model;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("user")
public class User implements Serializable, Identifiable
{
    private static final long serialVersionUID = -5252169753921361843L;
    	
	@Id
	private String id;
	@Indexed(unique = true)
	private String username;
	private String password;
	private String firstName;
	private String lastName;
	private Date creationDate;
	@NotNull
	private Status systemStatus;
	
	@DBRef
	private List<Role> role_id;
	@DBRef
	private Map<OptionalAttribute, String> optionalAttributes;
	@DBRef
    private List<Restriction> restrictions;

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
		this.setCreationDate();
		this.systemStatus = Status.ACTIVE;
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

	public Date getCreationDate() {
	    return creationDate;
	}

	private void setCreationDate() {
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		cal.setTimeInMillis(System.currentTimeMillis());
		creationDate = cal.getTime();
	}

	public Status getSystemStatus() {
	    return systemStatus;
	}

	public void setSystemStatus(Status systemStatus) {
	    this.systemStatus = systemStatus;
	}

}
package com.dvproject.vertTerm.Model;

import java.io.Serializable;
import java.time.Duration;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.validation.constraints.NotNull;

import com.dvproject.vertTerm.Service.AppointmentServiceImpl;
import com.dvproject.vertTerm.Service.BasicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("user")
public abstract class User extends Bookable implements Serializable
{
    private static final long serialVersionUID = -5252169753921361843L;

	@Indexed(unique = true)
	private String username;
	private String password;
	private String firstName;
	private String lastName;
	private Date creationDate;
	@NotNull
	private Status systemStatus;
	
	private List<OptionalAttributeWithValue> optionalAttributes;

	@DBRef
	private List<Role> roles;
	@DBRef
	private List<Restriction> restrictions;

	@PersistenceConstructor
	public User(String id, String username, String password, String firstName, String lastName, Status systemStatus, List<Role> roles,
			List<OptionalAttributeWithValue> optionalAttributes, List<Restriction> restrictions) {
		super(id);
		this.username = username;
		this.password = password;
		this.firstName = firstName;
		this.lastName = lastName;
		this.roles = roles;
		this.systemStatus = systemStatus;
		this.optionalAttributes = optionalAttributes;
		this.restrictions = restrictions;
	}

	public User(String id, String username, String password, String firstName, String lastName, List<Role> roles) {
		super(id);
		this.setCreationDate();
		this.systemStatus = Status.ACTIVE;
		this.username = username;
		this.password = password;
		this.firstName = firstName;
		this.lastName = lastName;
		this.roles = roles;
	}

	public User() {
		this.setCreationDate();
		this.systemStatus = Status.ACTIVE;
	}

	@Override
	public String toString() {
		return "User [id=" + this.getId() + ", firstName=" + firstName + ", lastName=" + lastName + "]";
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
		return roles;
	}

	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}

	public Date getCreationDate() {
		return creationDate;
	}
	
	public Status getSystemStatus() {
		return systemStatus;
	}

	public void setSystemStatus(Status systemStatus) {
		this.systemStatus = systemStatus;
	}

	public List<Restriction> getRestrictions() {
		return restrictions;
	}

	public void setRestrictions(List<Restriction> restrictions) {
		this.restrictions = restrictions;
	}

	private void setCreationDate() {
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		cal.setTimeInMillis(System.currentTimeMillis());
		creationDate = cal.getTime();
	}

	public List<OptionalAttributeWithValue> getOptionalAttributes() {
		return optionalAttributes;
	}

	public void setOptionalAttributes(List<OptionalAttributeWithValue> optionalAttributes) {
		this.optionalAttributes = optionalAttributes;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}
}
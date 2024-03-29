package com.dvproject.vertTerm.Service;

import com.dvproject.vertTerm.Model.*;
import com.dvproject.vertTerm.repository.RoleRepository;
import com.dvproject.vertTerm.repository.UserRepository;
import com.dvproject.vertTerm.security.SetupDataLoader;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserServiceImp extends WarningServiceImpl implements UserService {
	@Autowired
	private UserRepository repo;
	
	@Autowired
	private RoleRepository roleService;

	@Autowired
	private AppointmentService appointmentService;

	@Autowired
	private OptionalAttributesService optionalAttributesService;

	@Autowired
	private PasswordEncoder passwordEncoder;

	/**
	 * @author Robert Schulz
	 */
	@PreAuthorize("hasAuthority('USER_READ')")
	public List<User> getAll() {
		List<User> users = repo.findAll();
		return obfuscatePassword(users);
	}

	/**
	 * @author Robert Schulz
	 */
	@PreAuthorize("hasAuthority('USER_READ')")
	public List<User> getAll(Status status) {
		List<User> users = null;
		switch (status) {
			case ACTIVE:
				users = repo.findAllActive();
				break;
			case INACTIVE:
				users = repo.findAllInactive();
				break;
			case DELETED:
				users = repo.findAllDeleted();
				break;
		}
		return (users);
	}

	/**
	 * @author Robert Schulz
	 */
	@PreAuthorize("hasAuthority('USER_READ')")
	@Override
	public User getById(String id) {
		Optional<User> user = repo.findById(id);
		return user.map(this::obfuscatePassword).orElse(null);
	}

	/**
	 * @author Robert Schulz
	 */
	@Override
	public User create(User newInstance) {
		if (newInstance.getId() == null) {
			this.encodePassword(newInstance);
			this.testMandatoryFields(newInstance);
			return repo.save(newInstance);
		}
		if (repo.findById(newInstance.getId()).isPresent()) {
			throw new ResourceNotFoundException("Instance with the given id (" + newInstance.getId()
					+ ") exists on the database. Use the update method.");
		}
		return null;
	}

	@Override
	public User update(User updatedInstance) {
		User retVal = null;

		if (updatedInstance.getId() != null && repo.findById(updatedInstance.getId()).isPresent()) {
			this.testMandatoryFields(updatedInstance);
			this.encodePassword(updatedInstance);
			retVal = repo.save(updatedInstance);
		}
		
		return retVal;
	}

	@Override
	public boolean delete(String id) {
		User user = getById(id);

		testAppointments(id);
		user.obfuscate();

		user.setSystemStatus(Status.DELETED);
		repo.save(user);

		return getById(id).getSystemStatus() == Status.DELETED;
	}

	@PreAuthorize("hasAuthority('OWN_USER_READ')")
	public User getOwnUser(Principal principal) {
		User user = null;

		if (principal != null) {
			user = repo.findByUsername(principal.getName());
			user = obfuscatePassword(user);
		}

		return user;
	}

	@PreAuthorize("hasAuthority('USER_READ')")
	public List<User> getUsersWithUsernames(String[] usernames) {
		List<User> users = new ArrayList<>();

		for (String username : usernames) {
			users.add(repo.findByUsername(username));
		}

		return obfuscatePassword(users);
	}

	@PreAuthorize("hasAuthority('OWN_USER_ROLES_READ')")
	public List<Role> getOwnUserRoles(Principal principal) {
		User user = getOwnUser(principal);

		return user.getRoles();
	}

	@PreAuthorize("hasAuthority('OWN_USER_RIGHTS_READ')")
	public List<Right> getOwnUserRights(Principal principal) {
		return getRights(getOwnUser(principal));
	}

	@PreAuthorize("hasAuthority('ROLES_READ')")
	public List<Role> getUserRolesWithId(String id) {
		User user = getById(id);

		return user.getRoles();
	}

	@PreAuthorize("hasAuthority('RIGHTS_READ')")
	public List<Right> getUserRightsWithId(String id) {
		return getRights(getById(id));
	}
	
	
	// Everything below @author Joshua Müller
	private List<Right> getRights(User user) {
		List<Right> rights = new ArrayList<>();

		for (Role role : user.getRoles()) {
			for (Right right : role.getRights()) {
				if (!rights.contains(right))
					rights.add(right);
			}
		}

		return rights;
	}

	private List<User> obfuscatePassword(List<User> users) {
		return users.stream()
				.map(user -> obfuscatePassword(user))
				.collect(Collectors.toList());
	}

	private User obfuscatePassword(User user) {
		if (user != null)
			user.setPassword("");

		return user;
	}

	public void encodePassword(User user) {
		String password = user.getPassword();

		if (password == null || password.equals("")) {
			Optional<User> optUser = repo.findById(user.getId());

			if (optUser.isEmpty())
				throw new IllegalArgumentException("A user can not be created without a password!");

			user.setPassword(optUser.get().getPassword());
		} else {
			user.setPassword(passwordEncoder.encode(password));
		}
	}

	@Override
	public User getAnonymousUser() {
		String username = "anonymousUser";
		Role role = roleService.findByName(SetupDataLoader.ANONYMOUS_ROLE_NAME);
		User newUser = new User();

		// create unique username
		newUser.setId(new ObjectId().toHexString());
		newUser.setUsername(username + repo.count());
		newUser.setPassword(UUID.randomUUID().toString());
		newUser.setSystemStatus(Status.ACTIVE);
		newUser.setAnonymousUser(true);
		newUser.setRoles(Arrays.asList(role));

		return newUser;
	}

	public void testMandatoryFields(User user) {
		List<OptionalAttribute> optionalAttributes = new ArrayList<>(user.getOptionalAttributes());
		optionalAttributesService.testMandatoryFields(User.class.getSimpleName(), optionalAttributes);
	}

	public void testAppointments(String userid) {
		List<Appointment> appointments = appointmentService.getAppointmentsByUserIdAndAppointmentStatus(userid,
				AppointmentStatus.PLANNED);

		if (appointments != null && appointments.size() > 0)
			throw new IllegalArgumentException("User can not be deleted because he has booked appointments");
	}

	@Override
	List<Appointment> getPlannedAppointmentsWithId(String id) {
		return appointmentService.getAppointmentsByUserIdAndAppointmentStatus(id, AppointmentStatus.PLANNED);
	}

}

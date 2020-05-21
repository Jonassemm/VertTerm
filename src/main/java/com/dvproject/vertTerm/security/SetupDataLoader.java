package com.dvproject.vertTerm.security;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.dvproject.vertTerm.Model.Availability;
import com.dvproject.vertTerm.Model.AvailabilityRhythm;
import com.dvproject.vertTerm.Model.Customer;
import com.dvproject.vertTerm.Model.Employee;
import com.dvproject.vertTerm.Model.Right;
import com.dvproject.vertTerm.Model.Role;
import com.dvproject.vertTerm.Model.Status;
import com.dvproject.vertTerm.Model.User;
import com.dvproject.vertTerm.repository.CustomerRepository;
import com.dvproject.vertTerm.repository.EmployeeRepository;
import com.dvproject.vertTerm.repository.RightRepository;
import com.dvproject.vertTerm.repository.RoleRepository;
import com.dvproject.vertTerm.repository.UserRepository;

@Component
public class SetupDataLoader implements ApplicationListener<ContextRefreshedEvent> {
	private boolean alreadySetup = false;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private EmployeeRepository employeeRepository;
	
	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private RightRepository rightRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Transactional
	public void onApplicationEvent(ContextRefreshedEvent event) {
		if (alreadySetup)
			return;

		Role adminRole = createRoleIfNotFound("ADMIN_ROLE", setUpAdminRights());
		Role userRole = createRoleIfNotFound("ANONYMOUS_ROLE", setUpAnonymusUserRights());

		createEmployeeIfNotFound("admin", "password", Arrays.asList(adminRole));
		createUserIfNotFound("anonymousUser", "password", Arrays.asList(userRole));

		alreadySetup = true;
	}
	
	@Transactional
	private Right createRightIfNotFound(String name, String description) {
		Right right = rightRepository.findByName(name);

		if (right == null) {
			right = new Right();
			right.setName(name);
			right.setDescription(description);
			rightRepository.save(right);
		}

		return right;
	}

	private List<Right> setUpAdminRights() {
		List<Right> rights = new ArrayList<Right>();

		// user-rights
		rights.add(createRightIfNotFound("OWN_USER_DATA_READ", "Lesen der Benutzerdaten des eigenen Benutzers erlaubt"));
		rights.add(createRightIfNotFound("OWN_USER_DATA_WRITE", "Ändern der Benutzerdaten des eigenen Benutzers erlaubt"));
		rights.add(createRightIfNotFound("USERS_DATA_READ", "Lesen der Benutzerdaten aller Benutzer erlaubt"));
		rights.add(createRightIfNotFound("USER_STATUS_WRITE", "Ändern des Benutzerstatus erlaubt"));
		rights.add(createRightIfNotFound("USERS_DATA_WRITE", "Ändern der Benutzerdaten aller Benutzer erlaubt"));
		// user-role-rights
		rights.add(createRightIfNotFound("OWN_USER_ROLES_READ", "Lesen der Rollendaten des eigenen Benutzers erlaubt"));
		rights.add(createRightIfNotFound("USER_ROLES_READ", "Lesen der Benutzerrollen erlaubt"));
		rights.add(createRightIfNotFound("USER_ROLES_WRITE", "Ändern der Benutzerrollen erlaubt"));
		// user-right-rights
		rights.add(createRightIfNotFound("OWN_USER_RIGHTS_READ", "Lesen der Rechtedaten des eigenen Benutzers erlaubt"));
		rights.add(createRightIfNotFound("USER_RIGHTS_READ", "Lesen der Benutzerrechte erlaubt"));

		// role-rights
		rights.add(createRightIfNotFound("ROLES_WRITE", "Ändern der Rollendaten aller möglichen Rollen erlaubt"));
		rights.add(createRightIfNotFound("ROLES_READ", "Lesen der Rollendaten aller Rollen erlaubt"));
		rights.add(createRightIfNotFound("ROLE_RIGHTS_READ", "Lesen der Rechte aller Rollen erlaubt"));
		rights.add(createRightIfNotFound("ROLE_RIGHTS_WRITE", "Ändern der Rechte aller Rollen erlaubt"));

		// right-rights
		rights.add(createRightIfNotFound("RIGHTS_READ", "Lesen der Rechtedaten aller möglichen Rechte erlaubt"));

		return rights;
	}

	private List<Right> setUpAnonymusUserRights() {
		List<Right> rights = new ArrayList<Right>();

		rights.add(createRightIfNotFound("OWN_USER_DATA_READ", "Lesen der Benutzerdaten des eigenen Benutzers erlaubt"));

		return rights;
	}

	@Transactional
	private Role createRoleIfNotFound(String name, List<Right> rights) {
		Role role = roleRepository.findByName(name);

		if (role == null) {
			role = new Role();
			role.setName(name);
			role.setRights(rights);
			roleRepository.save(role);
		}

		return role;
	}

	@Transactional
	private User createUserIfNotFound(String username, String password, List<Role> roles) {
		User user = userRepository.findByUsername(username);

		if (user == null) {
			user = new User();
			user.setUsername(username);
			user.setPassword(passwordEncoder.encode(password));
			user.setRoles(roles);
			user.setSystemStatus(Status.ACTIVE);
			userRepository.save(user);
		}

		return user;
	}
	
	@Transactional
	private User createCustomerIfNotFound(String username, String password, List<Role> roles) {
		Customer user = customerRepository.findByUsername(username);

		if (user == null) {
			user = new Customer();
			user.setUsername(username);
			user.setPassword(passwordEncoder.encode(password));
			user.setRoles(roles);
			user.setSystemStatus(Status.ACTIVE);
			userRepository.save(user);
		}

		return user;
	}

	@Transactional
	private Employee createEmployeeIfNotFound(String username, String password, List<Role> roles) {
		Employee user = employeeRepository.findByUsername(username);

		if (user == null) {
			user = new Employee();
			user.setUsername(username);
			user.setPassword(passwordEncoder.encode(password));
			user.setSystemStatus(Status.ACTIVE);
			user.setRoles(roles);
			
			Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
			cal.set(2020, 5, 1, 8, 0, 0);
			Date start = cal.getTime();
			cal.clear();

			cal.set(2020, 5, 1, 18, 0, 0);
			Date end = cal.getTime();
			
			Availability avail = new Availability (start, end, AvailabilityRhythm.DAILY);
			user.setAvailabilities(Arrays.asList(avail));
			userRepository.save(user);
		}

		return user;
	}
}
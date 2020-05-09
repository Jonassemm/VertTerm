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
import com.dvproject.vertTerm.Model.AvailabilityRythm;
import com.dvproject.vertTerm.Model.Employee;
import com.dvproject.vertTerm.Model.Right;
import com.dvproject.vertTerm.Model.Role;
import com.dvproject.vertTerm.Model.Status;
import com.dvproject.vertTerm.Model.User;
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
	private Right createRightIfNotFound(String name) {
		Right rights = rightRepository.findByName(name);

		if (rights == null) {
			rights = new Right();
			rights.setName(name);
			rightRepository.save(rights);
		}

		return rights;
	}

	private List<Right> setUpAdminRights() {
		List<Right> rights = new ArrayList<Right>();

		// user-rights
		rights.add(createRightIfNotFound("OWN_USER_DATA_READ"));
		rights.add(createRightIfNotFound("OWN_USER_DATA_WRITE"));
		rights.add(createRightIfNotFound("USERS_DATA_READ"));
		rights.add(createRightIfNotFound("USERS_DATA_WRITE"));
		// user-role-rights
		rights.add(createRightIfNotFound("OWN_USER_ROLES_READ"));
		// user-right-rights
		rights.add(createRightIfNotFound("OWN_USER_RIGHTS_READ"));

		// role-rights
		rights.add(createRightIfNotFound("ROLES_WRITE"));
		rights.add(createRightIfNotFound("ROLES_READ"));

		// right-rights
		rights.add(createRightIfNotFound("RIGHTS_READ"));

		return rights;
	}

	private List<Right> setUpAnonymusUserRights() {
		List<Right> rights = new ArrayList<Right>();

		rights.add(createRightIfNotFound("OWN_USER_DATA_READ"));

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
			
			Availability avail = new Availability (start, end, AvailabilityRythm.DAILY);
			user.setAvailabilities(Arrays.asList(avail));
			userRepository.save(user);
		}

		return user;
	}
}
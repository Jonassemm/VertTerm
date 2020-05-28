package com.dvproject.vertTerm.security;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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
	
	private static Map <String, Right> rights = new HashMap<String, Right>();

	@Transactional
	public void onApplicationEvent(ContextRefreshedEvent event) {
		if (alreadySetup)
			return;
		
		setupRights();
		
		Role adminRole = createRoleIfNotFound("ADMIN_ROLE", setUpAdminRights());
		Role userRole = createRoleIfNotFound("ANONYMOUS_ROLE", setUpAnonymusUserRights());

		createEmployeeIfNotFound("admin", "password", Arrays.asList(adminRole));
		createUserIfNotFound("anonymousUser", "password", Arrays.asList(userRole));

		alreadySetup = true;
	}
	
	private void setupRights () {
		// user
		setupRight("OWN_USER_READ", "Lesen des eigenen Benutzers erlaubt");
		setupRight("OWN_USER_WRITE", "Ändern des eigenen Benutzers erlaubt");
		setupRight("USER_READ", "Lesen aller Benutzers erlaubt");
		setupRight("USER_WRITE", "Ändern aller Benutzers erlaubt");
		
		// employee
		setupRight("OWN_EMPLOYEE_READ", "Lesen des eigenen Angestellten erlaubt");
		setupRight("OWN_EMPLOYEE_WRITE", "Ändern des eigenen Angestellten erlaubt");
		setupRight("EMPLOYEE_READ", "Lesen aller Angestellter erlaubt");
		setupRight("EMPLOYEE_WRITE", "Ändern aller Angestellter erlaubt");
		
		// position
		setupRight("POSITION_READ", "Lesen aller Positionen erlaubt");
		setupRight("POSITION_WRITE", "Ändern aller Positionen erlaubt");
		
		// customer
		setupRight("OWN_CUSTOMER_READ", "Lesen des eigenen Kunden erlaubt");
		setupRight("OWN_CUSTOMER_WRITE", "Ändern des eigenen Kunden erlaubt");
		setupRight("CUSTOMER_READ", "Lesen aller Kunden erlaubt");
		setupRight("CUSTOMER_WRITE", "Ändern aller Kunden erlaubt");
		
		// role
		setupRight("ROLE_READ", "Lesen aller Kunden erlaubt");
		setupRight("ROLE_WRITE", "Ändern aller Kunden erlaubt");
		
		// right
		setupRight("RIGHT_READ", "Lesen aller Rechte erlaubt");
		
		// resource
		setupRight("RESOURCE_READ", "Lesen aller Ressourcen erlaubt");
		setupRight("RESOURCE_WRITE", "Ändern aller Ressourcen erlaubt");
		
		// resourceType
		setupRight("RESOURCE_TYPE_READ", "Lesen aller Ressourcentypen erlaubt");
		setupRight("RESOURCE_TYPE_WRITE", "Ändern aller Ressourcentypen erlaubt");
		
		// procedure
		setupRight("PROCEDURE_TYPE_READ", "Lesen aller Prozeduren erlaubt");
		setupRight("PROCEDURE_TYPE_WRITE", "Ändern aller Prozeduren erlaubt");
		
		// appointment
		setupRight("OWN_APPOINTMENT_READ", "Lesen der eigenen Termine erlaubt");
		setupRight("OWN_APPOINTMENT_WRITE", "Ändern der eigenen Termine erlaubt");
		setupRight("APPOINTMENT_READ", "Lesen aller Termine erlaubt");
		setupRight("APPOINTMENT_WRITE", "Ändern aller Termine erlaubt");
	}
	
	private void setupRight (String name, String description) {
		Right right = createRightIfNotFound(name, description);
		rights.put(right.getName(), right);
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
		List<Right> adminRights = new ArrayList<Right>();
		
		rights.forEach((name, right) -> adminRights.add(right));

		return adminRights;
	}

	private List<Right> setUpAnonymusUserRights() {
		List<Right> anonymousRights = new ArrayList<Right>();
		
		anonymousRights.add(rights.get("OWN_USER_READ"));
		
		return anonymousRights;
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
	private Customer createCustomerIfNotFound(String username, String password, List<Role> roles) {
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
			userRepository.save(user);
		}

		return user;
	}
}
package com.dvproject.vertTerm.security;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.dvproject.vertTerm.Model.*;
import com.dvproject.vertTerm.repository.*;

/**
 * @author Joshua Müller
 */
@Component
public class SetupDataLoader implements ApplicationListener<ContextRefreshedEvent> {
	public static final String ANONYMOUS_ROLE_NAME = "Anonymous_role";
	public static final String ADMIN_ROLE_NAME = "Admin_role";
	
	private boolean alreadySetup = false;
	private Map<String, Right> rights = new HashMap<>();

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private EmployeeRepository employeeService;

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private RightRepository rightRepository;

	@Autowired
	private OptionalAttributesRepository optionalAttributesRepository;

	@Autowired
	private AvailabilityRepository availablityService;

	@Autowired
	private OpeningHoursRepository openingHoursRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Transactional
	public void onApplicationEvent(ContextRefreshedEvent event) {
		if (alreadySetup)
			return;

		setupRights();

		Role adminRole = createRoleIfNotFound(ADMIN_ROLE_NAME, setUpAdminRights());
		Role userRole = createRoleIfNotFound(ANONYMOUS_ROLE_NAME, setUpAnonymusUserRights());

		createEmployeeIfNotFound("admin", "password", Arrays.asList(adminRole));
		createUserIfNotFound("anonymousUser", "password", Arrays.asList(userRole));

		setupOptionalAttributes();

		availablityService.save(new Availability("1", null, null, AvailabilityRhythm.ALWAYS));

		setupOpeningHours();

		alreadySetup = true;
	}

	private void setupRight(String name, String description) {
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

	public List<Right> setUpAdminRights() {
		List<Right> adminRights = new ArrayList<>();

		rights.forEach((name, right) -> adminRights.add(right));

		return adminRights;
	}

	public List<Right> setUpAnonymusUserRights() {
		List<Right> anonymousRights = new ArrayList<>();

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
		Employee user = employeeService.findByUsername(username).orElse(null);

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

	public void setupOptionalAttributes() {
		OptionalAttributes optionalAttributes = optionalAttributesRepository.findByClass("User");

		if (optionalAttributes == null) {
			optionalAttributes = new OptionalAttributes();

			optionalAttributes.setClassOfOptionalAttribut(User.class.getSimpleName());
			optionalAttributes.setOptionalAttributes(new ArrayList<>());
		}

		optionalAttributesRepository.save(optionalAttributes);
	}

	private void setupOpeningHours() {
		if (openingHoursRepository.count() == 1)
			return;

		openingHoursRepository.deleteAll();

		OpeningHours openingHours = new OpeningHours();

		openingHoursRepository.save(openingHours);
	}

	public void setupRights() {
		// user
		setupRight("OWN_USER_READ", "Lesen des eigenen Benutzers erlaubt");
		setupRight("OWN_USER_WRITE", "Ändern des eigenen Benutzers erlaubt");
		setupRight("USER_READ", "Lesen aller Benutzers erlaubt");
		setupRight("USER_WRITE", "Ändern aller Benutzers erlaubt");

		// employee
		setupRight("EMPLOYEE_READ", "Lesen aller Angestellter erlaubt");
		setupRight("EMPLOYEE_WRITE", "Ändern aller Angestellter erlaubt");

		// position
		setupRight("POSITION_READ", "Lesen aller Positionen erlaubt");
		setupRight("POSITION_WRITE", "Ändern aller Positionen erlaubt");

		// customer
		setupRight("CUSTOMER_READ", "Lesen aller Kunden erlaubt");
		setupRight("CUSTOMER_WRITE", "Ändern aller Kunden erlaubt");

		// role
		setupRight("ROLE_READ", "Lesen aller Rollen erlaubt");
		setupRight("ROLE_WRITE", "Ändern aller Rollen erlaubt");

		// right
		setupRight("RIGHT_READ", "Lesen aller Rechte erlaubt");

		// resource
		setupRight("RESOURCE_READ", "Lesen aller Ressourcen erlaubt");
		setupRight("RESOURCE_WRITE", "Ändern aller Ressourcen erlaubt");

		// resourceType
		setupRight("RESOURCE_TYPE_READ", "Lesen aller Ressourcentypen erlaubt");
		setupRight("RESOURCE_TYPE_WRITE", "Ändern aller Ressourcentypen erlaubt");

		// procedure
		setupRight("PROCEDURE_READ", "Lesen aller Prozeduren erlaubt");
		setupRight("PROCEDURE_WRITE", "Ändern aller Prozeduren erlaubt");

		// availability
		setupRight("OWN_AVAILABILITY_WRITE", "Ändern der eigenen Verfügbarkeiten erlaubt");
		setupRight("AVAILABILITY_WRITE", "Ändern aller Verfügbarkeiten erlaubt");
		
		// opening-hours
		setupRight("OPENING_HOURS_READ", "Lesen aller Öffnungszeiten erlaubt");
		setupRight("OPENING_HOURS_WRITE", "Ändern aller Öffnungszeiten erlaubt");

		// appointment
		setupRight("OWN_APPOINTMENT_READ", "Lesen der eigenen Termine erlaubt");
		setupRight("OWN_APPOINTMENT_WRITE", "Ändern der eigenen Termine erlaubt");
		setupRight("APPOINTMENT_READ", "Lesen aller Termine erlaubt");
		setupRight("APPOINTMENT_WRITE", "Ändern aller Termine erlaubt");

		setupRight("OVERRIDE",
				"Erlaubt das Durchführen einer Aktion, welche ansonsten aufgrund nicht eingehaltener Bedingungen nicht erlaubt wäre");
	}
}

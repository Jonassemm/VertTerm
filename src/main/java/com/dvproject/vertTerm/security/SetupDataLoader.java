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

import com.dvproject.vertTerm.Model.Availability;
import com.dvproject.vertTerm.Model.AvailabilityRhythm;
import com.dvproject.vertTerm.Model.Customer;
import com.dvproject.vertTerm.Model.Employee;
import com.dvproject.vertTerm.Model.OpeningHours;
import com.dvproject.vertTerm.Model.OptionalAttribute;
import com.dvproject.vertTerm.Model.OptionalAttributes;
import com.dvproject.vertTerm.Model.Right;
import com.dvproject.vertTerm.Model.Role;
import com.dvproject.vertTerm.Model.Status;
import com.dvproject.vertTerm.Model.User;
import com.dvproject.vertTerm.Service.EmployeeService;
import com.dvproject.vertTerm.repository.AvailabilityRepository;
import com.dvproject.vertTerm.repository.CustomerRepository;
import com.dvproject.vertTerm.repository.OpeningHoursRepository;
import com.dvproject.vertTerm.repository.OptionalAttributesRepository;
import com.dvproject.vertTerm.repository.RightRepository;
import com.dvproject.vertTerm.repository.RoleRepository;
import com.dvproject.vertTerm.repository.UserRepository;

@Component
public class SetupDataLoader implements ApplicationListener<ContextRefreshedEvent> {
	private boolean alreadySetup = false;
	private Map<String, Right> rights = new HashMap<>();

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private EmployeeService employeeService;

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

		Role adminRole = createRoleIfNotFound("ADMIN_ROLE", setUpAdminRights());
		Role userRole = createRoleIfNotFound("ANONYMOUS_ROLE", setUpAnonymusUserRights());

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
		Employee user = employeeService.getByUsername(username);

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
		setupRight("PROCEDURE_READ", "Lesen aller Prozeduren erlaubt");
		setupRight("PROCEDURE_WRITE", "Ändern aller Prozeduren erlaubt");
		setupRight("PROCEDURE_RELATION_WRITE", "Ändern aller Prozeduren erlaubt");

		// availability
		setupRight("OWN_AVAILABILITY_WRITE", "Ändern der eigenen VerfÃ¼gbarkeiten erlaubt");
		setupRight("AVAILABILITY_WRITE", "Ändern aller VerfÃ¼gbarkeiten erlaubt");

		// appointment
		setupRight("OWN_APPOINTMENT_READ", "Lesen der eigenen Termine erlaubt");
		setupRight("OWN_APPOINTMENT_WRITE", "Ändern der eigenen Termine erlaubt");
		setupRight("OWN_APPOINTMENT_BOOK", "Buchen der eigenen Termine erlaubt");
		setupRight("APPOINTMENT_READ", "Lesen aller Termine erlaubt");
		setupRight("APPOINTMENT_WRITE", "Ändern aller Termine erlaubt");
		setupRight("APPOINTMENT_BOOK", "Buchen aller Termine erlaubt");

		setupRight("OVERRIDE",
				"Erlaubt das Durchführen einer Aktion, welche ansonsten aufgrund nicht eingehaltener Bedingungen nicht erlaubt wäre");
	}
}

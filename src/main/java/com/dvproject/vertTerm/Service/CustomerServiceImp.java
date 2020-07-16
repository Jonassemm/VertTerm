package com.dvproject.vertTerm.Service;

import com.dvproject.vertTerm.Model.Customer;
import com.dvproject.vertTerm.Model.Status;
import com.dvproject.vertTerm.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CustomerServiceImp implements CustomerService {

	@Autowired
	CustomerRepository repo;

	@Autowired
	private UserService userService;

	/**
	 * @author Robert Schulz
	 */
	@PreAuthorize("hasAuthority('CUSTOMER_READ')")
	@Override
	public List<Customer> getAll() {
		return repo.findAll();
	}

	/**
	 * @author Robert Schulz
	 */
	@PreAuthorize("hasAuthority('CUSTOMER_READ')")
	public List<Customer> getAll(Status status) {
		List<Customer> users = null;
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
	@PreAuthorize("hasAuthority('CUSTOMER_READ')")
	@Override
	public Customer getById(String id) {
		Optional<Customer> appointment = repo.findById(id);
		return appointment.orElse(null);
	}

	/**
	 * @author Joshua Müller
	 */
	@Override
	@PreAuthorize("hasAuthority('CUSTOMER_WRITE')")
	public Customer create(Customer newInstance) {
		Customer retVal = null;

		if (newInstance.getId() == null) {
			userService.testMandatoryFields(newInstance);
			userService.encodePassword(newInstance);
			newInstance.setFirstName(capitalize(newInstance.getFirstName()));
			newInstance.setLastName(capitalize(newInstance.getLastName()));
			retVal = repo.save(newInstance);
		} else if (repo.findById(newInstance.getId()).isPresent())
			throw new ResourceNotFoundException("Instance with the given id (" + newInstance.getId()
					+ ") exists on the database. Use the update method.");

		return retVal;
	}

	/**
	 * @author Joshua Müller
	 */
	@Override
	@PreAuthorize("hasAuthority('CUSTOMER_WRITE')")
	public Customer update(Customer updatedInstance) {
		Customer retVal = null;

		if (updatedInstance.getId() != null && repo.findById(updatedInstance.getId()).isPresent()) {
			userService.testMandatoryFields(updatedInstance);
			userService.encodePassword(updatedInstance);
			updatedInstance.setFirstName(capitalize(updatedInstance.getFirstName()));
			updatedInstance.setLastName(capitalize(updatedInstance.getLastName()));
			retVal = repo.save(updatedInstance);

			userService.testWarningsFor(updatedInstance.getId());
		}

		return retVal;
	}

	/**
	 * @author Joshua Müller
	 */
	@Override
	@PreAuthorize("hasAuthority('CUSTOMER_WRITE')")
	public boolean delete(String id) {
		Customer user = getById(id);

		userService.testAppointments(id);
		user.obfuscate();

		user.setSystemStatus(Status.DELETED);

		return getById(id).getSystemStatus().isDeleted();
	}

	public static String capitalize(String str) {
		if (str == null)
			return str;
		return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();

	}
}

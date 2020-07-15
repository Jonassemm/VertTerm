package com.dvproject.vertTerm.Service;

import com.dvproject.vertTerm.Model.Restriction;
import com.dvproject.vertTerm.Model.User;
import com.dvproject.vertTerm.Model.OptionalAttribute;
import com.dvproject.vertTerm.Model.Procedure;
import com.dvproject.vertTerm.Model.Resource;
import com.dvproject.vertTerm.repository.ProcedureRepository;
import com.dvproject.vertTerm.repository.ResourceRepository;
import com.dvproject.vertTerm.repository.RestrictionRepository;
import com.dvproject.vertTerm.repository.UserRepository;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.fasterxml.jackson.core.sym.Name;

import net.springboot.javaguides.exception.ResourceExsistException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


/**  author Amar Alkhankan  **/
@Service
@Transactional
public class RestrictionServiceImp implements RestrictionService {

	@Autowired
	private RestrictionRepository RestrictionRepo;
	@Autowired
	private UserRepository UserRepo;
	@Autowired
	private ResourceRepository ResRepo;
	@Autowired
	private ProcedureRepository ProRepo;

	@PreAuthorize("hasAuthority('USER_WRITE')")
	public Restriction create(Restriction Restriction) {
		// create new restriction if not exist
		Restriction.setName(capitalize(Restriction.getName()));
		if (this.RestrictionRepo.findByName(Restriction.getName()) == null) {
			return RestrictionRepo.save(Restriction);
		} else {
			throw new ResourceNotFoundException(
					"Restriction with the given id :" + Restriction.getId() + "already exsist");
		}
	}

	@PreAuthorize("hasAuthority('USER_WRITE')")
	public Restriction update(Restriction Restriction) {
		// update a restriction if it's exist
		if (RestrictionRepo.findById(Restriction.getId()).isPresent()) {
			Restriction.setName(capitalize(Restriction.getName()));
			return RestrictionRepo.save(Restriction);
		} else {
			throw new ResourceNotFoundException("Restriction with the given id :" + Restriction.getId() + "not found");
		}
	}

	@PreAuthorize("hasAuthority('USER_READ')")
	public List<Restriction> getAll() {
		// get all restrictions from DB
		return this.RestrictionRepo.findAll();
	}

	@PreAuthorize("hasAuthority('USER_READ')")
	public Restriction getById(String id) {
		// get a restriction by the restriction-ID
		Optional<Restriction> RestrictionDb = this.RestrictionRepo.findById(id);
		if (RestrictionDb.isPresent()) {
			return RestrictionDb.get();
		} else {
			throw new ResourceNotFoundException("Restriction with the given id :" + id + " not found");
		}
	}

	@PreAuthorize("hasAuthority('USER_READ')")
	public List<Restriction> getRestrictions(String[] ids) {
		// get all restrictions if their ID exists in the given ids-list
		List<Restriction> Restrictions = new ArrayList<>();
		for (String id : ids) {
			Restrictions.add(this.getById(id));
		}

		return Restrictions;
	}

	public boolean delete(String id) {
		//delete a restriction by ID from DB
		Optional<Restriction> RestrictionDb = this.RestrictionRepo.findById(id);
		if (RestrictionDb.isPresent()) {
			this.RestrictionRepo.delete(RestrictionDb.get());
		} else {
			throw new ResourceNotFoundException("Restriction with the given id : " + id + " not found ");

		}
		return RestrictionRepo.existsById(id);
	}

	public boolean testRestrictions(List<Restriction> L1, List<Restriction> L2) {
		//test tow lists of restriction if they have the same restrictions
		boolean Result = true;
		for (Restriction elmL1 : L1) {
			String name1 = elmL1.getName();
			for (Restriction elmL2 : L2) {
				String name2 = elmL2.getName();
				if (name1.equals(name2)) {
					Result = false;
					break;
				}
			}
		}
		return Result;
	}

	//capitalize first letter of a string 
	public static String capitalize(String str) {
		if (str == null)
			return str;
		return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();

	}

}

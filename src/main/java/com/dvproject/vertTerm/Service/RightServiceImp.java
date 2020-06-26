package com.dvproject.vertTerm.Service;

import com.dvproject.vertTerm.Model.Right;
import com.dvproject.vertTerm.Model.Role;
import com.dvproject.vertTerm.Model.User;
import com.dvproject.vertTerm.repository.RightRepository;
import com.dvproject.vertTerm.repository.RoleRepository;
import com.dvproject.vertTerm.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class RightServiceImp implements RightService {

	@Autowired
	private RightRepository RightRepo;
	@Autowired
	private UserRepository userRepo;
	@Autowired
	private RoleRepository roleRepo;

	// @PreAuthorize("hasAuthority('RIGHT_READ')")
	public List<Right> getAll() {
		// get all rights from DB
		return this.RightRepo.findAll();
	}

	// @PreAuthorize("hasAuthority('RIGHT_READ')")
	public Right getById(String id) {
		// get a right by the right-ID
		Optional<Right> RightDb = this.RightRepo.findById(id);
		if (RightDb.isPresent()) {
			return RightDb.get();
		} else {
			throw new ResourceNotFoundException("Record not found with id : " + id);
		}
	}

	// @PreAuthorize("hasAuthority('ROLE_READ','USER_READ')")
	public List<User> getListUserswithRight(String id) {
		// get all user how they have the right with the given ID
		List<User> userslist = new ArrayList<>();
		List<User> Allusers = userRepo.findAll();
		for (User user : Allusers) {
			for (Role role : user.getRoles()) {
				for (Right right : role.getRights())
					if (right.getId().equals(id))
						if (!userslist.contains(user))
							userslist.add(user);
			}
		}
		return userslist;
	}

	// @PreAuthorize("hasAuthority('ROLE_READ','RIGHT_READ')")
	public List<Role> getListRoleswithRight(String id) {
		// get all roles that they have the right with the given ID
		List<Role> listrole = new ArrayList<>();
		List<Role> AllRoles = roleRepo.findAll();
		for (Role role : AllRoles) {
			for (Right right : role.getRights())
				if (right.getId().equals(id))
					if (!listrole.contains(role))
						listrole.add(role);
		}

		return listrole;
	}

}

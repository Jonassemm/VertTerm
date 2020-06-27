package com.dvproject.vertTerm.Service;

import com.dvproject.vertTerm.Model.Right;
import com.dvproject.vertTerm.Model.Role;
import com.dvproject.vertTerm.Model.User;
import com.dvproject.vertTerm.repository.RightRepository;
import com.dvproject.vertTerm.repository.RoleRepository;
import com.dvproject.vertTerm.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class RoleServiceImp implements RoleService {

	@Autowired
	private RoleRepository RoleRepo;
	@Autowired
	private UserRepository userRepo;
	@Autowired
	private RightRepository rightRepo;

	// @PreAuthorize("hasAuthority('ROLE_WRITE')")
	public Role create(Role role) {
		//create new Role if not exist
		role.setName(capitalize(role.getName()));
		if (this.RoleRepo.findByName(role.getName()) == null) {
			return RoleRepo.save(role);
		} else {
			throw new ResourceNotFoundException("Role with the given id :" + role.getId() + "already exist");
		}
	}

	// @PreAuthorize("hasAuthority('ROLE_WRITE')")
	public Role update(Role role) {
		//update role if it's exist
		if (RoleRepo.findById(role.getId()).isPresent()) {
			role.setName(capitalize(role.getName()));
			return RoleRepo.save(role);
		} else {
			throw new ResourceNotFoundException("Role with the given id :" + role.getId() + "not found");
		}
	}

	// @PreAuthorize("hasAuthority('ROLE_READ')")
	public List<Role> getAll() {
		//get all role from DB
		return this.RoleRepo.findAll();
	}

	// @PreAuthorize("hasAuthority('ROLE_READ')")
	public Role getById(String id) {
		//get a role by the role-ID
		Optional<Role> RoleDb = this.RoleRepo.findById(id);
		if (RoleDb.isPresent()) {
			return RoleDb.get();
		} else {
			throw new ResourceNotFoundException("Role with the given id :" + id + " not found");
		}
	}

	// @PreAuthorize("hasAuthority('ROLE_READ')")
	public List<Role> getRoles(String[] ids) {
		//get all roles if their ID exists in the given ids-list
		List<Role> Roles = new ArrayList<>();
		for (String id : ids) {
			Roles.add(this.getById(id));
		}
		return Roles;
	}

	// @PreAuthorize("hasAuthority('ROLE_READ')")
	public boolean delete(String id) {
		//delete a role by ID from DB
		Optional<Role> RoleDb = this.RoleRepo.findById(id);
		if (RoleDb.isPresent()) {
			this.RoleRepo.delete(RoleDb.get());
		} else {
			throw new ResourceNotFoundException("Role with the given id : " + id + " not found ");

		}
		return RoleRepo.existsById(id);
	}

	// @PreAuthorize("hasAuthority('ROLE_READ','RIGHT_READ')")
	public List<Right> getRoleRights(String id) {
		//Get all rights for the role with the given ID
		List<Right> rights = new ArrayList<>();
		Role role = getById(id);
		for (Right right : role.getRights())
			if (!rights.contains(right))
				rights.add(right);
		return rights;
	}

	// @PreAuthorize("hasAuthority('USER_READ')")
	public List<User> getRoleUsers(String id) {
		//get all users that they have the role with the given ID
		List<User> userslist = new ArrayList<>();
		List<User> Allusers = userRepo.findAll();
		for (User user : Allusers) {
			for (Role role : user.getRoles()) {
				if (role.getId().equals(id))
					if (!userslist.contains(user))
						userslist.add(user);
			}
		}
		return userslist;
	}

	// @PreAuthorize("hasAuthority('ROLE_WRITE','RIGHT_READ')")
	public List<Right> updateRoleRights(String id, String[] Rids) {
		//update the rights of the role with the given ID
		//rights of the role = get all rights if their ID exists in the given Rids-list
		List<Right> rights = new ArrayList<>();
		List<Right> Allrights = rightRepo.findAll();
		List<String> RightIds = List.of(Rids);
		for (Right r : Allrights) {
			String rid = r.getId();
			if (RightIds.contains(rid)) {
				if (!rights.contains(r))
					rights.add(r);
			}
		}

		Optional<Role> RoleDb = this.RoleRepo.findById(id);
		if (RoleDb.isPresent()) {
			Role RoleUpdate = RoleDb.get();
			RoleUpdate.setRights(rights);
			RoleRepo.save(RoleUpdate);
			return rights;
		} else {
			throw new ResourceNotFoundException("Role with the given id :" + id + " not found");
		}
	}

	// @PreAuthorize("hasAuthority('ROLE_READ','USER_WRITE')")
	public List<User> updateRoleUsers(String id, String[] Uids) {
		//Add the role with the given ID to all users if their ID exists in the given Uids-list
		//and remove that role from the other users (their ID not exists in Uids-list) only if
		//the user have more then one role
		List<User> Addusers = new ArrayList<>();
		List<User> Removeusers = new ArrayList<>();
		List<User> userslist = new ArrayList<>();
		Role role = this.getById(id);
		List<User> Allusers = userRepo.findAll();
		List<String> UserIds = List.of(Uids);
		List<String> roles = new ArrayList<>();

		for (User user : Allusers) {
			String uid = user.getId();
			for (Role r : user.getRoles()) {
				roles.add(r.getId());
			}
			if (UserIds.contains(uid)) {
				if (!(roles.contains(id))) {
					if (!Addusers.contains(user))
						Addusers.add(user);
				}

			} else if (roles.size() > 1) {
				if (roles.contains(id)) {
					if (!Removeusers.contains(user))
						Removeusers.add(user);
				}

			}
			roles = new ArrayList<>();
		}

		List<User> users2add = AddRole(role, Addusers);
		List<User> users2remove = RemoveRole(id, Removeusers);

		for (User u : users2remove)
			userslist.add(u);

		for (User u : users2add)
			userslist.add(u);

		return userslist;
	}

	public List<User> AddRole(Role role, List<User> users) {
		List<User> userslist = new ArrayList<>();
		for (User u : users) {
			List<Role> Userroles = u.getRoles();
			Optional<User> UserDB = this.userRepo.findById(u.getId());
			Userroles.add(role);
			User UserRoleUpdate = UserDB.get();
			UserRoleUpdate.setRoles(Userroles);
			userRepo.save(UserRoleUpdate);
			userslist.add(UserRoleUpdate);
		}
		return userslist;
	}

	public List<User> RemoveRole(String id, List<User> users) {

		List<User> userslist = new ArrayList<>();
		for (User u : users) {
			List<Role> Userroles = new ArrayList<>();
			List<String> r = new ArrayList<>();
			for (Role roles : u.getRoles()) {
				r.add(roles.getId());
				if (!(r.contains(id))) {
					if (!Userroles.contains(roles))
						Userroles.add(roles);
				}
			}

			Optional<User> UserDB = this.userRepo.findById(u.getId());
			User UserRoleUpdate = UserDB.get();
			UserRoleUpdate.setRoles(Userroles);
			userRepo.save(UserRoleUpdate);
			userslist.add(u);

		}

		return userslist;
	}

	//capitalize first letter of a string 
	public static String capitalize(String str) {
		if (str == null)
			return str;
		// return str.toUpperCase() ;
		return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
	}
}

package com.dvproject.vertTerm.Service;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.dvproject.vertTerm.Model.Right;
import com.dvproject.vertTerm.Model.Role;
import com.dvproject.vertTerm.Model.User;
import com.dvproject.vertTerm.repository.UserRepository;

@Service
public class UserServiceImp implements UserService {
    @Autowired
    private UserRepository userRepository;

    @PreAuthorize("hasAuthority('USERS_DATA_READ')")
    public List<User> getAllUsers() {
	List<User> users = userRepository.findAll();
	return obfuscatePassword(users);
    }

    @PreAuthorize("hasAuthority('OWN_USER_DATA_READ')")
    public User getOwnUser(Principal principal) {
	User user = null;
	
	if (principal != null) {
	    user = userRepository.findByUsername(principal.getName());
	    user = obfuscatePassword(user);
	}

	return user;
    }

    @PreAuthorize("hasAuthority('USERS_DATA_READ')")
    public List<User> getUsersWithUsernames(String[] usernames) {
	List<User> users = new ArrayList<>();

	for (String username : usernames) {
	    users.add(userRepository.findByUsername(username));
	}

	return obfuscatePassword(users);
    }

    @PreAuthorize("hasAuthority('USERS_DATA_READ')")
    public User getUserWithId(String id) {
	Optional<User> user = userRepository.findById(id);
	return user.map(this::obfuscatePassword).orElse(null);
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
	User user = getUserWithId(id);

	return user.getRoles();
    }

    @PreAuthorize("hasAuthority('RIGHTS_READ')")
    public List<Right> getUserRightsWithId(String id) {
	return getRights(getUserWithId(id));
    }

    private List<Right> getRights(User user) {
	List<Right> rights = new ArrayList<>();

	for (Role role : user.getRoles()) {
	    for (Right right : role.getRights()) {
		if (!rights.contains(right)) {
		    rights.add(right);
		}
	    }
	}

	return rights;
    }

    private List<User> obfuscatePassword(List<User> users) {
	for (User user : users) {
	    user = obfuscatePassword(user);
	}
	return users;
    }

    private User obfuscatePassword(User user) {
	if (user != null) {
	    user.setPassword("");
	}

	return user;
    }

}
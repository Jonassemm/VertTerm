package com.dvproject.vertTerm.Service;

import com.dvproject.vertTerm.Model.*;
import com.dvproject.vertTerm.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImp implements UserService {
    @Autowired
    private UserRepository repo;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    //@PreAuthorize("hasAuthority('USERS_DATA_READ')")
    public List<User> getAll() {
	List<User> users = repo.findAll();
	return obfuscatePassword(users);
    }

    //@PreAuthorize("hasAuthority('USERS_DATA_READ')")
	public List<User> getAll(Status status) {
		List<User> users = null;
		switch(status){
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

	//@PreAuthorize("hasAuthority('USERS_DATA_READ')")
	@Override
	public User getById(String id) {
		Optional<User> user = repo.findById(id);
		return user.map(this::obfuscatePassword).orElse(null);
	}

	@Override
	public User create(User newInstance) {
		if (newInstance.getId() == null) {
			this.encodePassword(newInstance);
			return repo.save(newInstance);
		}
		if (repo.findById(newInstance.getId()).isPresent()) {
			throw new ResourceNotFoundException("Instance with the given id (" + newInstance.getId() + ") exists on the database. Use the update method.");
		}
		return null;
	}

	@Override
	public User update(User updatedInstance) {
		if (updatedInstance.getId() != null && repo.findById(updatedInstance.getId()).isPresent()) {
			if (this.hasPasswordChanged(updatedInstance))
				this.encodePassword(updatedInstance);
			
			return repo.save(updatedInstance);
		}
		return null;
	}

	@Override
	public boolean delete(String id) {
		repo.deleteById(id);
		return repo.existsById(id);
	}

	@PreAuthorize("hasAuthority('OWN_USER_DATA_READ')")
    public User getOwnUser(Principal principal) {
	User user = null;
	
	if (principal != null) {
	    user = repo.findByUsername(principal.getName());
	    user = obfuscatePassword(user);
	}

	return user;
    }

    @PreAuthorize("hasAuthority('USERS_DATA_READ')")
    public List<User> getUsersWithUsernames(String[] usernames) {
	List<User> users = new ArrayList<>();

	for (String username : usernames) {
	    users.add(repo.findByUsername(username));
	}

	return obfuscatePassword(users);
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
	User user = getById(id);

	return user.getRoles();
    }

    @PreAuthorize("hasAuthority('RIGHTS_READ')")
    public List<Right> getUserRightsWithId(String id) {
	return getRights(getById(id));
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
    
    public void encodePassword (User user) {
    	String password = user.getPassword();
    	
    	if (password == null || password.equals("")) {
    		user.setPassword(this.getById(user.getId()).getPassword());
    	} else {
    		String encodedPassword = passwordEncoder.encode(password);
    		user.setPassword(encodedPassword);
    	}
    }
    
    public boolean hasPasswordChanged (User user) {
    	User oldUser = this.getById(user.getId());
    	return !oldUser.getPassword().equals(user.getPassword());
    }

	@Override
	public User getAnonymousUser() {
		String username = "anonymousUser";
		User user = this.getUsersWithUsernames(new String [] {username}).get(0);
		
		//delete the id, so mongodb creates a new one 
		user.setId(null);
		//create unique username
		user.setUsername(username + repo.count());
		user.setPassword("{noop}" + UUID.randomUUID().toString());
		user.setSystemStatus(Status.ACTIVE);
		
		return user;
	}

}
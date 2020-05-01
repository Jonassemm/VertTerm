package com.dvproject.vertTerm.security;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.dvproject.vertTerm.Model.Right;
import com.dvproject.vertTerm.Model.Role;
import com.dvproject.vertTerm.Model.User;
import com.dvproject.vertTerm.repository.RightRepository;
import com.dvproject.vertTerm.repository.RoleRepository;
import com.dvproject.vertTerm.repository.UserRepository;

@Component
public class SetupDataLoader implements ApplicationListener<ContextRefreshedEvent> 
{
    
    boolean alreadySetup = false;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private RightRepository rightRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public void onApplicationEvent(ContextRefreshedEvent event) 
    {
	if (alreadySetup)
	    return;

	Right readRight = createRightIfNotFound("READ_RIGHT");
	Right writeRight = createRightIfNotFound("WRITE_RIGHT");
	Right userWriteRight = createRightIfNotFound("USERS_WRITE_RIGHT");
	Right usersWriteRight = createRightIfNotFound("USERS_READ_RIGHT");

	List<Right> adminRights = Arrays.asList(readRight, writeRight, userWriteRight, usersWriteRight);
	List<Right> userRights = Arrays.asList(readRight, usersWriteRight);
	Role adminRole = createRoleIfNotFound("ROLE_ADMIN", adminRights);
	Role userRole = createRoleIfNotFound("ROLE_USER", userRights);
	
	createUserIfNotFound("admin", "password", Arrays.asList(adminRole));
	createUserIfNotFound("user", "password", Arrays.asList(userRole));
	
	alreadySetup = true;
    }

    @Transactional
    private Right createRightIfNotFound(String name) 
    {
	Right rights = rightRepository.findByName(name);

	if (rights == null) 
	{
	    rights = new Right();
	    rights.setName(name);
	    rightRepository.save(rights);
	}

	return rights;
    }

    @Transactional
    private Role createRoleIfNotFound(String name, List<Right> rights)
    {
	Role role = roleRepository.findByName(name);

	if (role == null) 
	{
	    role = new Role();
	    role.setName(name);
	    role.setRights(rights);
	    roleRepository.save(role);
	}

	return role;
    }
    
    @Transactional
    private User createUserIfNotFound (String username, String password, List<Role> roles) {
	User user = userRepository.findByUsername(username);

	if (user == null) 
	{
	    user = new User();
	    user.setUsername(username);
	    user.setPassword(passwordEncoder.encode(password));
	    user.setRoles(roles);
	    userRepository.save(user);
	}

	return user;
    }
}
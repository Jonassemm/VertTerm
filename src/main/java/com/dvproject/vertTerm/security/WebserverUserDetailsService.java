package com.dvproject.vertTerm.security;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.dvproject.vertTerm.Model.*;
import com.dvproject.vertTerm.repository.UserRepository;

/**
 * @author Joshua Müller
 */

@Component
public class WebserverUserDetailsService implements UserDetailsService {
	@Autowired
	private UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = findUserInDataBase(username);

		return new org.springframework.security.core.userdetails.User(
				user.getUsername(), 
				user.getPassword(),
				user.getSystemStatus().isActive() && !user.isAnonymousUser(), 
				true, 
				true, 
				true,
				getAuthorities(user.getRoles()));
	}

	public UserDetails loadAnonymousUserByUsername(String username) throws UsernameNotFoundException {
		User user = findUserInDataBase(username);

		return new org.springframework.security.core.userdetails.User(
				user.getUsername(), 
				user.getPassword(),
				user.getSystemStatus().isActive(), 
				true, 
				true, 
				true, 
				getAuthorities(user.getRoles()));
	}
	
	public User findUserInDataBase(String username) {
		User user = userRepository.findByUsername(username);

		if (user == null) { throw new UsernameNotFoundException("No user with the given username"); }
		
		return user;
	}

	public List<? extends GrantedAuthority> getAuthorities(List<Role> roles) {
		return getGrantedAuthorities(getRights(roles));
	}

	private List<String> getRights(List<Role> roles) {
		List<String> rightIdentifier = new ArrayList<>();
		List<Right> rights = new ArrayList<>();

		roles.forEach(role -> rights.addAll(role.getRights()));
		rights.forEach(right -> rightIdentifier.add(right.getName()));

		return rightIdentifier;
	}

	private List<GrantedAuthority> getGrantedAuthorities(List<String> rights) {
		return rights.stream()
						.map(right -> new SimpleGrantedAuthority(right))
						.collect(Collectors.toList());
	}
}

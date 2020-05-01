package com.dvproject.vertTerm.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.dvproject.vertTerm.Model.Right;
import com.dvproject.vertTerm.Model.Role;
import com.dvproject.vertTerm.Model.User;
import com.dvproject.vertTerm.repository.UserRepository;

@Component
public class MyUserDetailsService implements UserDetailsService 
{
    @Autowired
    private UserRepository userRepository;

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException 
    {
	User user = userRepository.findByUsername(username);
	
	if (user == null) 
	{
	    throw new UsernameNotFoundException ("No user with the given username");
	}
	
	return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), true,
		true, true, true, getAuthorities(user.getRoles()));
    }

    private Collection<? extends GrantedAuthority> getAuthorities(Collection<Role> roles) 
    {
	return getGrantedAuthorities(getRights(roles));
    }

    private List<String> getRights(Collection<Role> roles) 
    {
	List<String> rights = new ArrayList<String>();
	List<Right> collection = new ArrayList<Right>();
	for (Role role : roles) 
	{
	    collection.addAll(role.getRights());
	}
	for (Right item : collection) 
	{
	    rights.add(item.getName());
	}
	
	return rights;
    }

    private List<GrantedAuthority> getGrantedAuthorities(List<String> rights) 
    {
	List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
	for (String right : rights) 
	{
	    authorities.add(new SimpleGrantedAuthority(right));
	}
	
	return authorities;
    }
}
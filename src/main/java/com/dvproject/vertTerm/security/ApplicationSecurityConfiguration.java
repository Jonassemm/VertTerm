package com.dvproject.vertTerm.security;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import com.dvproject.vertTerm.Model.Right;
import com.dvproject.vertTerm.Model.Role;
import com.dvproject.vertTerm.repository.RoleRepository;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class ApplicationSecurityConfiguration extends WebSecurityConfigurerAdapter 
{
    @Autowired
    private MyUserDetailsService userDetailsService;
    
    @Autowired
    private RoleRepository roleReposiroty;

    @Override
    protected void configure(HttpSecurity http) throws Exception 
    {
	http.formLogin()
		.loginPage("/login").permitAll()
		.loginProcessingUrl("/login").permitAll()
		.usernameParameter("username").passwordParameter("password")
		.failureHandler(new AuthenticationFailureHandler () {
		    @Override
		    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			    AuthenticationException exception) throws IOException, ServletException {
			response.sendError(401, "Failure to log in");
		    }
		    
		})
		.successHandler(new AuthenticationSuccessHandler () {
		    @Override
		    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			    Authentication authentication) throws IOException, ServletException {
			response.setStatus(HttpServletResponse.SC_OK);
			
		    }
		    
		});;
	
	http.anonymous().authorities(getAuthoritiesOfAnonymousUsers());
	
	http.authorizeRequests()
		.antMatchers("/**").permitAll()
		.anyRequest().authenticated();
	
	http.logout()
		.logoutUrl("/logout")
		.clearAuthentication(true)
		.invalidateHttpSession(true);
	
	http.csrf().disable();
	
	http.headers().frameOptions().disable();
    }
    
    @Override
    protected void configure(final AuthenticationManagerBuilder auth) throws Exception 
    {
        auth.userDetailsService(userDetailsService);
    }
    
    private List<GrantedAuthority> getAuthoritiesOfAnonymousUsers () 
    {
	List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority> ();
	Role anonymousRole = roleReposiroty.findByName("ANONYMOUS_ROLE");
	
	if (anonymousRole == null)
	{
	    authorities.add(new SimpleGrantedAuthority("NO_RIGHTS"));
	}
	else
	{
	    for (Right right : anonymousRole.getRights())
	    {
		authorities.add(new SimpleGrantedAuthority(right.getName()));
	    }
	}
	
	return authorities;
    }

}
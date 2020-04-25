package com.dvproject.vertTerm.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class ApplicationSecurityConfiguration extends WebSecurityConfigurerAdapter 
{
    @Autowired
    private MyUserDetailsService userDetailsService;

    @Override
    protected void configure(HttpSecurity http) throws Exception 
    {
	http.formLogin()
		.loginPage("/login").permitAll()
		.loginProcessingUrl("/login").permitAll()
		.usernameParameter("username").passwordParameter("password")
		.defaultSuccessUrl("/")
		.failureForwardUrl("/login?error");
	
	http.authorizeRequests()
		.antMatchers("/test").hasAuthority("WRITE_RIGHT")
		.antMatchers("/**").permitAll()
		.anyRequest().authenticated();
	
	http.logout()
		.logoutUrl("/logout")
		.clearAuthentication(true)
		.invalidateHttpSession(true)
		.logoutSuccessUrl("/login");
	
	http.csrf().disable();
	
	http.headers().frameOptions().disable();
    }
    
    @Override
    protected void configure(final AuthenticationManagerBuilder auth) throws Exception 
    {
        auth.userDetailsService(userDetailsService);
    }

}
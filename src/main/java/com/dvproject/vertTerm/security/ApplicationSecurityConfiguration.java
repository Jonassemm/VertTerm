package com.dvproject.vertTerm.security;

import java.io.PrintWriter;
import java.util.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.mapping.event.ValidatingMongoEventListener;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.dvproject.vertTerm.Model.*;
import com.dvproject.vertTerm.repository.*;

/**
 * @author Joshua Müller
 */

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class ApplicationSecurityConfiguration extends WebSecurityConfigurerAdapter {
	@Autowired
	private WebserverUserDetailsService userDetailsService;

	@Autowired
	private RoleRepository roleReposiroty;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private SetupDataLoader setupData;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.formLogin().loginPage("/login").permitAll().loginProcessingUrl("/login").permitAll()
				.usernameParameter("username").passwordParameter("password")
				.failureHandler((request, response, exception) -> response.sendError(401, "Failure to log in"))
				.successHandler((request, response, authentication) -> {
					response.setStatus(HttpServletResponse.SC_OK);
					PrintWriter out = response.getWriter();
					out.write(userRepository.findByUsername(authentication.getName()).getId());
					out.flush();
				});

		http.anonymous().authorities(getAuthoritiesOfAnonymousUsers());

		http.authorizeRequests().antMatchers("/**").permitAll().anyRequest().authenticated();

		http.exceptionHandling().authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED));

		http.logout().logoutUrl("/logout")
				.logoutSuccessHandler((request, response, authentication) -> { 
						SecurityContextHolder.getContext().setAuthentication(null);
						response.setStatus(200); 
					})
				.deleteCookies("JSESSIONID").clearAuthentication(true).invalidateHttpSession(true);

		http.csrf().disable();

		http.headers().disable();

		http.cors();
	}

	@Override
	protected void configure(final AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService);
	}

	private List<GrantedAuthority> getAuthoritiesOfAnonymousUsers() {
		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		Role anonymousRole = roleReposiroty.findByName("Anonymous_role");
		List<Right> rights = null;

		if (anonymousRole == null) {
			setupData.setupRights();
			rights = setupData.setUpAnonymusUserRights();
		} else {
			rights = anonymousRole.getRights();
		}

		rights.forEach(right -> authorities.add(new SimpleGrantedAuthority(right.getName())));

		return authorities;
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

		configuration.setAllowedOrigins(Arrays.asList("*"));
		configuration.setAllowedMethods(Arrays.asList("*"));
		configuration.setAllowedHeaders(Arrays.asList("*"));
		configuration.setExposedHeaders(Arrays.asList("Set-Cookie", "exception", "appointmentid", "starttime"));
		configuration.setAllowCredentials(true);

		source.registerCorsConfiguration("/**", configuration);
		return source;
	}

	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer()
		{
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/**").allowedHeaders("*").allowedOrigins("*").allowedMethods("*");
			}

			@Override
			public void addResourceHandlers(ResourceHandlerRegistry registry) {
				registry.addResourceHandler("/").addResourceLocations("/index.html");
			}
		};
	}

	@Bean
	public Validator getValidator() {
		LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
		return validator;
	}

	@Bean
	public ValidatingMongoEventListener validatingMongoEventListener() {
		return new ValidatingMongoEventListener(getValidator());
	}

}

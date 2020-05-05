package com.dvproject.vertTerm.security;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
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
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.dvproject.vertTerm.Model.Right;
import com.dvproject.vertTerm.Model.Role;
import com.dvproject.vertTerm.repository.RoleRepository;
import com.dvproject.vertTerm.repository.UserRepository;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class ApplicationSecurityConfiguration extends WebSecurityConfigurerAdapter 
{
    @Autowired
    private MyUserDetailsService userDetailsService;
    
    @Autowired
    private RoleRepository roleReposiroty;
    
    @Autowired
    private UserRepository userRepository;

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
			PrintWriter out = response.getWriter();
			out.write(userRepository.findByUsername(authentication.getName()).getId());
			out.flush();
		    }
		});
	
	http.anonymous().authorities(getAuthoritiesOfAnonymousUsers());
	
	http.authorizeRequests()
		.antMatchers("/**").permitAll()
		.anyRequest().authenticated()
		.and().exceptionHandling().authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED));
	
	http.logout()
		.logoutUrl("/logout")
		.logoutSuccessHandler(new LogoutSuccessHandler () {
		    @Override
		    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response,
			    Authentication authentication) throws IOException, ServletException {
			response.setStatus(200);
		    }
		})
		.deleteCookies("JSESSIONID")
		.clearAuthentication(true)
		.invalidateHttpSession(true);
	
	http.csrf().disable();
	
	http.cors();
	
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
    
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() 
    {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("*"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
    
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**").allowedHeaders("*").allowedOrigins("*").allowedMethods("*");
            }
        };
    }

}
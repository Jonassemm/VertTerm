package com.dvproject.vertTerm.Controller;

import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.dvproject.vertTerm.Model.User;
import com.dvproject.vertTerm.repository.UserRepository;
import com.dvproject.vertTerm.security.WebserverUserDetailsService;

/**
 * @author Joshua Müller
 */
@Controller
public class HomeController {
	@Autowired
	private WebserverUserDetailsService manager;

	@Autowired
	private UserRepository userService;
	
	@Autowired
	private PasswordEncoder passwordEncoder;

	@GetMapping("/login/{base64Login}")
	public String login(@PathVariable String base64Login) {
		LoginInformation loginInformation = new LoginInformation(base64Login);
		
		String username = loginInformation.getUsername();
		String password = loginInformation.getPassword();
		
		User user = userService.findByUsername(username);

		if (!user.isAnonymousUser() && !user.getPassword().equals(passwordEncoder.encode(password)))
			throw new IllegalArgumentException("User can not be logged in");

		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
				username, null, manager.getAuthorities(user.getRoles()));
		SecurityContextHolder.getContext().setAuthentication(authenticationToken);

		return "redirect:/";
	}
	
	private static class LoginInformation {
		private String username;
		private String password;
		
		public LoginInformation(String base64String) {
			String loginString = new String (Base64.getDecoder().decode(base64String));

			String [] loginInformation = loginString.split(",");
			
			if (loginInformation.length != 2)
				throw new IllegalArgumentException("Logininformation is wrong");
			
			username = loginInformation [0];
			password = loginInformation [1];
		}
		
		public String getUsername() {
			return username;
		}
		
		public String getPassword() {
			return password;
		}
	}
}

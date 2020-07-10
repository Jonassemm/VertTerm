package com.dvproject.vertTerm.Controller;

import java.util.Base64;
import java.util.Base64.Decoder;

import javax.servlet.http.HttpServletRequest;

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

@Controller
public class HomeController {
	@Autowired
	private WebserverUserDetailsService manager;

	@Autowired
	private UserRepository userService;
	
	@Autowired
	private PasswordEncoder passwordEncoder;

	@GetMapping("/")
	public String index() {
		return "index";
	}

	@GetMapping("/login/{login}")
	public String login(HttpServletRequest request, @PathVariable String login) {
		LoginInformation loginInformation = new LoginInformation(login);
		
		String username = loginInformation.getUsername();
		String password = loginInformation.getPassword();
		
		User user = userService.findByUsername(username);

		if (!user.isAnonymousUser() && !user.getPassword().equals(passwordEncoder.encode(password)))
			throw new IllegalArgumentException("User can not be logged in");

		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
				username, null, manager.getAuthorities(user.getRoles()));
		SecurityContextHolder.getContext().setAuthentication(authenticationToken);

		return "index";
	}
	
	private static class LoginInformation {
		private String username;
		private String password;
		
		public LoginInformation(String base64String) {
			Decoder base64Decoder = Base64.getDecoder();
			String loginString = base64Decoder.decode(base64String.getBytes()).toString();
			
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

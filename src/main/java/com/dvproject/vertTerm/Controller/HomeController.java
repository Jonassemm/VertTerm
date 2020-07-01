package com.dvproject.vertTerm.Controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

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

	@RequestMapping(value = "/")
	public String index() {
		return "index";
	}

	@ResponseBody
	@GetMapping("/")
	public String login(HttpServletRequest request, @RequestParam String username, @RequestParam String password) {
		User user = userService.findByUsername(username);

		if (!user.isAnonymousUser() && !user.getPassword().equals(passwordEncoder.encode(password)))
			throw new IllegalArgumentException("User can not be logged in");

		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
				username, null, manager.getAuthorities(user.getRoles()));
		SecurityContextHolder.getContext().setAuthentication(authenticationToken);

		return user.getId();
	}
}

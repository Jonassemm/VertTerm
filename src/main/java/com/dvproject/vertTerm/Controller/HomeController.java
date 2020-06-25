package com.dvproject.vertTerm.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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

	@RequestMapping(value = "/")
	public String index() {
		return "index";
	}
	
	@ResponseBody
	@GetMapping("/")
	public String login (@RequestParam String username, @RequestParam String password) {
		UserDetails userDetails = manager.loadAnonymousUserByUsername(username);
		User user = userService.findByUsername(username);
		
		if (!user.isAnonymousUser())
			throw new IllegalArgumentException("User is not an anonymous User");
		
		Authentication auth = new UsernamePasswordAuthenticationToken (userDetails.getUsername(), userDetails.getPassword());
		SecurityContextHolder.getContext().setAuthentication(auth);
		
		return user.getId();
	}
}

package com.dvproject.vertTerm.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class HomeController {
	@Autowired
	private UserDetailsService manager; 

	@RequestMapping(value = "/")
	public String index() {
		return "index";
	}
	
	@GetMapping("/")
	public RedirectView login (@RequestParam String username, @RequestParam String password) {
		UserDetails userDetails = manager.loadUserByUsername(username);
		Authentication auth = new UsernamePasswordAuthenticationToken (userDetails.getUsername(), userDetails.getPassword());
		SecurityContextHolder.getContext().setAuthentication(auth);
		
		RedirectView redirectView = new RedirectView();
	    redirectView.setUrl("http://localhost:3001/");
	    return redirectView;
	}
}

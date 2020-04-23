package com.dvproject.vertTerm.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomeController {

	@RequestMapping(value = "/")
	public String index() {
		return "index";
	}
	
	/*
	 * testmethod for rights
	*/
	@RequestMapping(value = "/test")
	public String test() {
		return "test";
	}
}

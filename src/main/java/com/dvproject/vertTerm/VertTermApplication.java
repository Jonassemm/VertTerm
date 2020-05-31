package com.dvproject.vertTerm;

import java.util.TimeZone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;

import com.dvproject.vertTerm.Controller.HomeController;

@SpringBootApplication
//@ComponentScan(basePackageClasses=HomeController.class)
public class VertTermApplication extends SpringBootServletInitializer{

	public static void main(String[] args) {
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
		SpringApplication.run(VertTermApplication.class, args);
	}

}

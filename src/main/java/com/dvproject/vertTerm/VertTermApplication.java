package com.dvproject.vertTerm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackageClasses=HomeController.class)
public class VertTermApplication extends SpringBootServletInitializer{

	public static void main(String[] args) {
		SpringApplication.run(VertTermApplication.class, args);
	}

}

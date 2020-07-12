package com.dvproject.vertTerm;

import java.util.TimeZone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;

import com.dvproject.vertTerm.Controller.HomeController;

@SpringBootApplication
@EnableCaching(proxyTargetClass = true)
//@ComponentScan(basePackageClasses=HomeController.class)
public class VertTermApplication extends SpringBootServletInitializer{
	
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(VertTermApplication.class);
    }

	public static void main(String[] args) {
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
		SpringApplication.run(VertTermApplication.class, args);
	}

}

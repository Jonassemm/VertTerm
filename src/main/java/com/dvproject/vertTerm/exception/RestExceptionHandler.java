package com.dvproject.vertTerm.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import net.springboot.javaguides.exception.ResourceNotFoundException;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {
	@ExceptionHandler(ResourceNotFoundException.class)
	private ResponseEntity<Object> handleRessourceNotFoundException (ResourceNotFoundException exception){
		return new ResponseEntity<>(exception, HttpStatus.NOT_FOUND);
	}
}

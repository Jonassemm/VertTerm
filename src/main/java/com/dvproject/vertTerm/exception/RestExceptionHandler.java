package com.dvproject.vertTerm.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import net.springboot.javaguides.exception.ResourceNotFoundException;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {
	@ExceptionHandler(ResourceNotFoundException.class)
	private ResponseEntity<Object> handleRessourceNotFoundException(ResourceNotFoundException exception) {
		return new ResponseEntity<>(exception, HttpStatus.NOT_FOUND);
	}
	
	@ExceptionHandler(NullPointerException.class)
	private ResponseEntity<Object> handleRessourceNotFoundException(NullPointerException exception) {
		return new ResponseEntity<>(exception, HttpStatus.UNPROCESSABLE_ENTITY);
	}
	
	@ExceptionHandler(IllegalArgumentException.class)
	private ResponseEntity<Object> handleIllegalArgumentException (IllegalArgumentException exception){
		return new ResponseEntity<>(exception, HttpStatus.BAD_REQUEST);
	}

//	@Override
//	protected ResponseEntity<Object> handleNoHandlerFoundException(
//			NoHandlerFoundException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
//		headers.add("Location", "/api/");    
//		return new ResponseEntity<Object>(headers, HttpStatus.FOUND);
//	 }
}

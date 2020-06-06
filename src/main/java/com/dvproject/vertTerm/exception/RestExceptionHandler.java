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
	public ResponseEntity<Object> handleRessourceNotFoundException(ResourceNotFoundException exception) {
		return new ResponseEntity<>(exception, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(NullPointerException.class)
	public ResponseEntity<Object> handleRessourceNotFoundException(NullPointerException exception, WebRequest request) {
		return handleExceptionInternal(exception, exception.getMessage(), new HttpHeaders(),
				HttpStatus.UNPROCESSABLE_ENTITY, request);
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<Object> handleIllegalArgumentException(IllegalArgumentException exception,
			WebRequest request) {
		return handleExceptionInternal(exception, exception.getMessage(), new HttpHeaders(), HttpStatus.BAD_REQUEST,
				request);
	}

	@Override
	protected ResponseEntity<Object> handleNoHandlerFoundException(NoHandlerFoundException ex, HttpHeaders headers,
			HttpStatus status, WebRequest request) {
		headers.add("Location", "http://localhost:3001/");
		return new ResponseEntity<Object>(headers, HttpStatus.OK);
	}

	@ExceptionHandler(javax.validation.ConstraintViolationException.class)
	public ResponseEntity<Object> handleConstraintViolationException(
			javax.validation.ConstraintViolationException exception, WebRequest request) {
		return handleExceptionInternal(exception, exception.getMessage(), new HttpHeaders(), HttpStatus.BAD_REQUEST,
				request);
	}

	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<Object> handleRuntimeException(RuntimeException exception, WebRequest request) {
		return handleExceptionInternal(exception, exception.getMessage(), new HttpHeaders(),
				HttpStatus.UNPROCESSABLE_ENTITY, request);
	}
}

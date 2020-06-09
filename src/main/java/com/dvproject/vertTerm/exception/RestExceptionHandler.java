package com.dvproject.vertTerm.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.dvproject.vertTerm.Model.Appointment;
import com.dvproject.vertTerm.Model.Employee;
import com.dvproject.vertTerm.Model.Position;
import com.dvproject.vertTerm.Model.Procedure;
import com.dvproject.vertTerm.Model.ProcedureRelation;
import com.dvproject.vertTerm.Model.Resource;
import com.dvproject.vertTerm.Model.ResourceType;
import com.dvproject.vertTerm.Model.User;

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

	@ExceptionHandler(ResourceTypeException.class)
	public ResponseEntity<Object> handleResourceTypeException(ResourceTypeException exception, WebRequest request) {
		ResourceType resourcetype = exception.getResourceType();
		StringBuilder builder = new StringBuilder(exception.getMessage());
		HttpHeaders headers = addExceptionHeader(null, resourcetype.getClass().getSimpleName());
		
		builder.append(": resourcetype ");
		builder.append(resourcetype.getName());

		return handleExceptionInternal(exception, builder.toString(), headers, HttpStatus.UNPROCESSABLE_ENTITY, request);
	}

	@ExceptionHandler(ResourceException.class)
	public ResponseEntity<Object> handleResourceException(ResourceException exception, WebRequest request) {
		Resource resource = exception.getResource();
		StringBuilder builder = new StringBuilder(exception.getMessage());
		HttpHeaders headers = addExceptionHeader(null, resource.getClass().getSimpleName());
		
		builder.append(": resource ");
		builder.append(resource.getName());

		return handleExceptionInternal(exception, builder.toString(), headers, HttpStatus.UNPROCESSABLE_ENTITY, request);
	}

	@ExceptionHandler(ProcedureRelationException.class)
	public ResponseEntity<Object> handleProcedureRelationException(ProcedureRelationException exception, WebRequest request) {
		ProcedureRelation procedurerelation = exception.getProcedureRelation();
		StringBuilder builder = new StringBuilder(exception.getMessage());
		HttpHeaders headers = addExceptionHeader(null, procedurerelation.getClass().getSimpleName());
		
		builder.append(": procedurerelation ");
		builder.append(procedurerelation.getProcedure().getName());

		return handleExceptionInternal(exception, builder.toString(), headers, HttpStatus.UNPROCESSABLE_ENTITY, request);
	}
	
	@ExceptionHandler(ProcedureException.class)
	public ResponseEntity<Object> handleProcedureException(ProcedureException exception, WebRequest request) {
		Procedure procedure = exception.getProcedure();
		StringBuilder builder = new StringBuilder(exception.getMessage());
		HttpHeaders headers = addExceptionHeader(null, procedure.getClass().getSimpleName());
		
		builder.append(": procedure ");
		builder.append(procedure.getName());

		return handleExceptionInternal(exception, builder.toString(), headers, HttpStatus.UNPROCESSABLE_ENTITY, request);
	}
	
	@ExceptionHandler(EmployeeException.class)
	public ResponseEntity<Object> handleEmployeeException(EmployeeException exception, WebRequest request) {
		Employee employee = exception.getEmployee();
		StringBuilder builder = new StringBuilder(exception.getMessage());
		HttpHeaders headers = addExceptionHeader(null, employee.getClass().getSimpleName());
		
		builder.append(": employee ");
		builder.append(employee.getFirstName());
		builder.append(" ");
		builder.append(employee.getLastName());

		return handleExceptionInternal(exception, builder.toString(), headers, HttpStatus.UNPROCESSABLE_ENTITY, request);
	}
	
	@ExceptionHandler(BookedCustomerException.class)
	public ResponseEntity<Object> handleBookedCustomerException(BookedCustomerException exception, WebRequest request) {
		User user = exception.getUser();
		StringBuilder builder = new StringBuilder(exception.getMessage());
		HttpHeaders headers = addExceptionHeader(null, user.getClass().getSimpleName());
		
		builder.append(": user ");
		builder.append(user.getUsername());

		return handleExceptionInternal(exception, builder.toString(), headers, HttpStatus.UNPROCESSABLE_ENTITY, request);
	}
	
	@ExceptionHandler(PositionException.class)
	public ResponseEntity<Object> handlePositionException(PositionException exception, WebRequest request) {
		Position position = exception.getPosition();
		StringBuilder builder = new StringBuilder(exception.getMessage());
		HttpHeaders headers = addExceptionHeader(null, position.getClass().getSimpleName());
		
		builder.append(": position ");
		builder.append(position.getName());

		return handleExceptionInternal(exception, builder.toString(), headers, HttpStatus.UNPROCESSABLE_ENTITY, request);
	}
	
	@ExceptionHandler(AppointmentTimeException.class)
	public ResponseEntity<Object> handleAppointmentTimeException(AppointmentTimeException exception, WebRequest request) {
		Appointment appointment = exception.getAppointment();
		StringBuilder builder = new StringBuilder(exception.getMessage());
		HttpHeaders headers = addExceptionHeader(null, "appointment");
		
		builder.append(": procedure ");
		builder.append(appointment.getBookedProcedure().getName());
		builder.append(", planned starttime ");
		builder.append(appointment.getPlannedStarttime());
		builder.append(", planned endtime ");
		builder.append(appointment.getPlannedEndtime());

		return handleExceptionInternal(exception, builder.toString(), headers, HttpStatus.UNPROCESSABLE_ENTITY, request);
	}

	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<Object> handleRuntimeException(RuntimeException exception, WebRequest request) {
		return handleExceptionInternal(exception, exception.getMessage(), new HttpHeaders(),
				HttpStatus.UNPROCESSABLE_ENTITY, request);
	}

	@Override
	public ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers,
			HttpStatus status, WebRequest request) {
		return handleExceptionInternal(ex, ex.getMessage(), headers, HttpStatus.UNPROCESSABLE_ENTITY, request);
	}

	private HttpHeaders addExceptionHeader(HttpHeaders headers, String value) {
		if (headers == null) {
			headers = new HttpHeaders();
		}

		headers.add("exception", value);

		return headers;
	}
}

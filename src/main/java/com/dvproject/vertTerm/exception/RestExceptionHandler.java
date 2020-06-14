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
import com.dvproject.vertTerm.Model.Restriction;
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

	@ExceptionHandler(RestrictionException.class)
	public ResponseEntity<Object> handleRestrictionException(RestrictionException exception, WebRequest request) {
		Restriction restriction = exception.getRestriction();
		StringBuilder builder = new StringBuilder(exception.getMessage());
		HttpHeaders headers = addExceptionHeader(null, restriction.getClass().getSimpleName());
		
		if (restriction != null) {
			builder.append(": restriction ");
			builder.append(restriction.getName());
		}

		return handleExceptionInternal(exception, builder.toString(), headers, HttpStatus.UNPROCESSABLE_ENTITY,
				request);
	}

	@ExceptionHandler(AppointmentException.class)
	public ResponseEntity<Object> handleAppointmentException(AppointmentException exception, WebRequest request) {
		Appointment appointment = exception.getAppointment();
		StringBuilder builder = new StringBuilder(exception.getMessage());
		HttpHeaders headers = addExceptionHeader(null, appointment.getClass().getSimpleName());
		
		if (appointment != null) {
			builder.append(": appointment from procedure ");
			builder.append(appointment.getBookedProcedure().getName());
		}

		return handleExceptionInternal(exception, builder.toString(), headers, HttpStatus.UNPROCESSABLE_ENTITY,
				request);
	}

	@ExceptionHandler(AvailabilityException.class)
	public ResponseEntity<Object> handleAvailabilityException(AvailabilityException exception, WebRequest request) {
		StringBuilder builder = new StringBuilder(exception.getMessage());
		HttpHeaders headers = addExceptionHeader(null, "Availability");

		return handleExceptionInternal(exception, builder.toString(), headers, HttpStatus.UNPROCESSABLE_ENTITY,
				request);
	}

	@ExceptionHandler(ResourceTypeException.class)
	public ResponseEntity<Object> handleResourceTypeException(ResourceTypeException exception, WebRequest request) {
		ResourceType resourcetype = exception.getResourceType();
		StringBuilder builder = new StringBuilder(exception.getMessage());
		HttpHeaders headers = addExceptionHeader(null, resourcetype.getClass().getSimpleName());

		if (resourcetype != null) {
			builder.append(": resourcetype ");
			builder.append(resourcetype.getName());
		}

		return handleExceptionInternal(exception, builder.toString(), headers, HttpStatus.UNPROCESSABLE_ENTITY,
				request);
	}

	@ExceptionHandler(ResourceException.class)
	public ResponseEntity<Object> handleResourceException(ResourceException exception, WebRequest request) {
		Resource resource = exception.getResource();
		StringBuilder builder = new StringBuilder(exception.getMessage());
		HttpHeaders headers = addExceptionHeader(null, resource.getClass().getSimpleName());

		if (resource != null) {
			builder.append(": resource ");
			builder.append(resource.getName());
		}

		return handleExceptionInternal(exception, builder.toString(), headers, HttpStatus.UNPROCESSABLE_ENTITY,
				request);
	}

	@ExceptionHandler(ProcedureRelationException.class)
	public ResponseEntity<Object> handleProcedureRelationException(ProcedureRelationException exception,
			WebRequest request) {
		ProcedureRelation procedurerelation = exception.getProcedureRelation();
		StringBuilder builder = new StringBuilder(exception.getMessage());
		HttpHeaders headers = addExceptionHeader(null, procedurerelation.getClass().getSimpleName());

		if (procedurerelation != null) {
			builder.append(": procedurerelation ");
			builder.append(procedurerelation.getProcedure().getName());
		}

		return handleExceptionInternal(exception, builder.toString(), headers, HttpStatus.UNPROCESSABLE_ENTITY,
				request);
	}

	@ExceptionHandler(ProcedureException.class)
	public ResponseEntity<Object> handleProcedureException(ProcedureException exception, WebRequest request) {
		Procedure procedure = exception.getProcedure();
		StringBuilder builder = new StringBuilder(exception.getMessage());
		HttpHeaders headers = addExceptionHeader(null, procedure.getClass().getSimpleName());

		if (procedure != null) {
			builder.append(": procedure ");
			builder.append(procedure.getName());
		}

		return handleExceptionInternal(exception, builder.toString(), headers, HttpStatus.UNPROCESSABLE_ENTITY,
				request);
	}

	@ExceptionHandler(EmployeeException.class)
	public ResponseEntity<Object> handleEmployeeException(EmployeeException exception, WebRequest request) {
		Employee employee = exception.getEmployee();
		StringBuilder builder = new StringBuilder(exception.getMessage());
		HttpHeaders headers = addExceptionHeader(null, employee.getClass().getSimpleName());

		if (employee != null) {
			builder.append(": employee ");
			builder.append(employee.getFirstName());
			builder.append(" ");
			builder.append(employee.getLastName());
		}

		return handleExceptionInternal(exception, builder.toString(), headers, HttpStatus.UNPROCESSABLE_ENTITY,
				request);
	}

	@ExceptionHandler(BookedCustomerException.class)
	public ResponseEntity<Object> handleBookedCustomerException(BookedCustomerException exception, WebRequest request) {
		User user = exception.getUser();
		StringBuilder builder = new StringBuilder(exception.getMessage());
		HttpHeaders headers = addExceptionHeader(null, user.getClass().getSimpleName());

		if (user != null) {
			builder.append(": user ");
			builder.append(user.getUsername());
		}

		return handleExceptionInternal(exception, builder.toString(), headers, HttpStatus.UNPROCESSABLE_ENTITY,
				request);
	}

	@ExceptionHandler(PositionException.class)
	public ResponseEntity<Object> handlePositionException(PositionException exception, WebRequest request) {
		Position position = exception.getPosition();
		StringBuilder builder = new StringBuilder(exception.getMessage());
		HttpHeaders headers = addExceptionHeader(null, position.getClass().getSimpleName());

		if (position != null) {
			builder.append(": position ");
			builder.append(position.getName());
		}

		return handleExceptionInternal(exception, builder.toString(), headers, HttpStatus.UNPROCESSABLE_ENTITY,
				request);
	}

	@ExceptionHandler(AppointmentTimeException.class)
	public ResponseEntity<Object> handleAppointmentTimeException(AppointmentTimeException exception,
			WebRequest request) {
		Appointment appointment = exception.getAppointment();
		StringBuilder builder = new StringBuilder(exception.getMessage());
		HttpHeaders headers = addExceptionHeader(null, "appointment");

		if (appointment != null) {
			builder.append(": procedure ");
			builder.append(appointment.getBookedProcedure().getName());
			builder.append(", planned starttime ");
			builder.append(appointment.getPlannedStarttime());
			builder.append(", planned endtime ");
			builder.append(appointment.getPlannedEndtime());
		}

		return handleExceptionInternal(exception, builder.toString(), headers, HttpStatus.UNPROCESSABLE_ENTITY,
				request);
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
		headers.add("Access-Control-Expose-Headers", "exception");

		return headers;
	}
}

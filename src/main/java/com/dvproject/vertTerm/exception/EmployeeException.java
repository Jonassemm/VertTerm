package com.dvproject.vertTerm.exception;

import com.dvproject.vertTerm.Model.Employee;

public class EmployeeException extends RuntimeException{
	private static final long serialVersionUID = 5505464839233329858L;
	
	private final Employee failedEmployee;

	public EmployeeException(String message, Employee employee) {
		super(message);
		this.failedEmployee = employee;
	}

	public Employee getEmployee() {
		return failedEmployee;
	}
}

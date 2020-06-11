package com.dvproject.vertTerm.exception;

import com.dvproject.vertTerm.Model.User;

public class BookedCustomerException extends RuntimeException {
	private static final long serialVersionUID = -4718176959272485899L;
	
	private User customer;
	
	public BookedCustomerException (String message, User customer) {
		super(message);
		this.customer = customer;
	}

	public User getUser() {
		return customer;
	}
	
}

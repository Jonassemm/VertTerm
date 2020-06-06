package com.dvproject.vertTerm.exception;

import com.dvproject.vertTerm.Model.Customer;

public class BookedCustomerException extends RuntimeException {
	private static final long serialVersionUID = -4718176959272485899L;
	
	private Customer customer;
	
	public BookedCustomerException (String message, Customer customer) {
		super(message);
		this.customer = customer;
	}

	public Customer getCustomer() {
		return customer;
	}
	
}

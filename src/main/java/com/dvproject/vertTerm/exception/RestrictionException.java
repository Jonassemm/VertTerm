package com.dvproject.vertTerm.exception;

import com.dvproject.vertTerm.Model.Restriction;

/**
 * @author Joshua Müller
 */
public class RestrictionException extends RuntimeException {
	private static final long serialVersionUID = 3038008596740670594L;

	private Restriction restriction;

	public RestrictionException(String message, Restriction restriction) {
		super(message);
		this.restriction = restriction;
	}

	public Restriction getRestriction() {
		return restriction;
	}

}

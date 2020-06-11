package com.dvproject.vertTerm.exception;

import com.dvproject.vertTerm.Model.Procedure;

public class ProcedureException extends RuntimeException {
	private static final long serialVersionUID = -8905393641692950581L;
	
	private Procedure position;
	
	public ProcedureException (String message, Procedure position) {
		super(message);
		this.position = position;
	}

	public Procedure getProcedure() {
		return position;
	}

}

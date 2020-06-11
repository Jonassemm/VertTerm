package com.dvproject.vertTerm.exception;

import com.dvproject.vertTerm.Model.ProcedureRelation;

public class ProcedureRelationException extends RuntimeException {
	private static final long serialVersionUID = -2128588524530407610L;
	
	private final ProcedureRelation failedProcedureRelation;

	public ProcedureRelationException(String message, ProcedureRelation procedureRelation) {
		super(message);
		this.failedProcedureRelation = procedureRelation;
	}

	public ProcedureRelation getProcedureRelation() {
		return failedProcedureRelation;
	}
}

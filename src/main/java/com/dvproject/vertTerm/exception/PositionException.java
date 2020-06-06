package com.dvproject.vertTerm.exception;

import com.dvproject.vertTerm.Model.Position;

public class PositionException extends RuntimeException {
	private static final long serialVersionUID = -1305403394107278319L;
	
	private Position position;
	
	public PositionException (String message, Position position) {
		super(message);
		this.position = position;
	}

	public Position getPosition() {
		return position;
	}
}

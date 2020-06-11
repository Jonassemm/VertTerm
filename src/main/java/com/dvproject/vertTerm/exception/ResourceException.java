package com.dvproject.vertTerm.exception;

import com.dvproject.vertTerm.Model.Resource;

public class ResourceException extends RuntimeException{
	private static final long serialVersionUID = 1252852610143557036L;
	
	private final Resource failedResource;

	public ResourceException(String message, Resource resource) {
		super(message);
		this.failedResource = resource;
	}

	public Resource getResource() {
		return failedResource;
	}
}

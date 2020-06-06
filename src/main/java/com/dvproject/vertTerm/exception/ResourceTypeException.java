package com.dvproject.vertTerm.exception;

import com.dvproject.vertTerm.Model.ResourceType;

public class ResourceTypeException extends RuntimeException {
	private static final long serialVersionUID = 9036720560983407603L;
	
	private ResourceType resourceType;
	
	public ResourceTypeException (String message, ResourceType resourceType) {
		super(message);
		this.resourceType = resourceType;
	}

	public ResourceType getResourceType() {
		return resourceType;
	}
	
}

package com.dvproject.vertTerm.Model;

/**
 * @author Joshua Müller
 */
public class OptionalAttributeWithValue extends OptionalAttribute {
	
	private String value;
	
	public OptionalAttributeWithValue() {
		super();
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}

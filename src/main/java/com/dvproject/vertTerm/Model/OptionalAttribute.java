package com.dvproject.vertTerm.Model;

public class OptionalAttribute {
	private String name;
	private boolean mandatoryField;
	
	public OptionalAttribute() {}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public boolean isMandatoryField() {
		return mandatoryField;
	}
	
	public void setMandatoryField(boolean mandatoryField) {
		this.mandatoryField = mandatoryField;
	}

}

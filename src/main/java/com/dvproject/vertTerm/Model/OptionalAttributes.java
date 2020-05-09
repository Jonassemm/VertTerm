package com.dvproject.vertTerm.Model;

import java.util.List;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class OptionalAttributes {
	@NotEmpty
	private List<OptionalAttribute> optionalAttributes;
	@NotNull
	private String classOfOptionalAttribut;
	
	public List<OptionalAttribute> getOptionalAttributes() {
		return optionalAttributes;
	}
	
	public void setOptionalAttributes(List<OptionalAttribute> optionalAttributes) {
		this.optionalAttributes = optionalAttributes;
	}
	
	public String getClassOfOptionalAttribut() {
		return classOfOptionalAttribut;
	}
	
	public void setClassOfOptionalAttribut(String classOfOptionalAttribut) {
		this.classOfOptionalAttribut = classOfOptionalAttribut;
	}

}

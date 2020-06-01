package com.dvproject.vertTerm.Model;

import java.io.Serializable;
import java.util.List;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class OptionalAttributes implements Serializable {
	private static final long serialVersionUID = 7741132277124076940L;
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

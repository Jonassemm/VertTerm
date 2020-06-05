package com.dvproject.vertTerm.Model;

import java.io.Serializable;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class OptionalAttributes implements Serializable {
	private static final long serialVersionUID = 7741132277124076940L;
	@NotNull
	private List<OptionalAttribute> optionalAttributes;
	@NotNull
	@Indexed(unique = true)
	private String classOfOptionalAttribut;
    private String  id;
    
    public OptionalAttributes () {}
    
    public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
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
	
	public void testMandatoryFields (List<OptionalAttribute> attributesToTest) {
		for (OptionalAttribute optionalAttribute : optionalAttributes) {
			//test if the optional attribute is mandatory and not set to a value
			if (optionalAttribute.isMandatoryField() && !attributesToTest.contains(optionalAttribute)) {
				throw new IllegalArgumentException("Not all mandatory optional attributes are set");
			}
		}
	}

}

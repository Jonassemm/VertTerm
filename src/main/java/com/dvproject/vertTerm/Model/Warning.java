package com.dvproject.vertTerm.Model;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum Warning {
	@JsonProperty("ResourceWarning")
	RESOURCE_WARNING,
	@JsonProperty("noWarning")
	NO_WARNING,
	@JsonProperty("EmplyeeWarning")
	EMPLOYEE_WARNING,
	@JsonProperty("ProcedureWarning")
	PROCEDURE_WARNING,
	@JsonProperty("UserWarning")
	USER_WARNING;
}

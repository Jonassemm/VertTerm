package com.dvproject.vertTerm.Model;

import java.io.Serializable;

import org.springframework.data.mongodb.core.mapping.Document;

@Document("ressource")
public class Building extends Resource implements Serializable {
	private static final long serialVersionUID = -6728481952004088157L;
}

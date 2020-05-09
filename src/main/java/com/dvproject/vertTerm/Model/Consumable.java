package com.dvproject.vertTerm.Model;

import java.io.Serializable;

import org.springframework.data.mongodb.core.mapping.Document;

@Document("ressource")
public class Consumable extends Resource implements Serializable {
	private static final long serialVersionUID = -6141132277124076940L;
	
	private int numberOfUses;
	private int pricePerUnit;
	
	public Consumable () {
		numberOfUses = 1;
	}

	public int getNumberOfUses() {
		return numberOfUses;
	}

	public void setNumberOfUses(int numberOfUses) {
		this.numberOfUses = numberOfUses;
	}

	public int getPricePerUnit() {
		return pricePerUnit;
	}

	public void setPricePerUnit(int pricePerUnit) {
		this.pricePerUnit = pricePerUnit;
	}
}

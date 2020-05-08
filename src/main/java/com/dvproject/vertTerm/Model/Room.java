package com.dvproject.vertTerm.Model;

import java.io.Serializable;

import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import com.mongodb.lang.NonNull;

@Document("ressource")
public class Room extends Resource implements Serializable {
	private static final long serialVersionUID = 4674628310429192035L;
	
	private int floor;
	private int roomNr;
	
	@DBRef
	@NonNull
	private Building building;

	public int getFloor() {
		return floor;
	}

	public void setFloor(int floor) {
		this.floor = floor;
	}

	public int getRoomNr() {
		return roomNr;
	}

	public void setRoomNr(int roomNr) {
		this.roomNr = roomNr;
	}

	public Building getBuilding() {
		return building;
	}

	public void setBuilding(Building building) {
		this.building = building;
	}
}

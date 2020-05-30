package com.dvproject.vertTerm.Service;

import com.dvproject.vertTerm.Model.Status;

public class StatusService {

	public static boolean isUpdateable (Status status) {
		return status != Status.DELETED;
	}
	
	public static boolean isInsertable (Status status) {
		return status == Status.ACTIVE;
	}
}

package com.dvproject.vertTerm.Service;

import com.dvproject.vertTerm.Model.Status;

/**
 * @author Joshua Müller
 */
public class StatusService {

	public static boolean isUpdateable (Status status) {
		return !status.isDeleted();
	}
	
	public static boolean isInsertable (Status status) {
		return status.isActive();
	}
}

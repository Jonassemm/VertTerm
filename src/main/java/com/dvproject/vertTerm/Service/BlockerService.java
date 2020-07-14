package com.dvproject.vertTerm.Service;

import java.util.List;

import com.dvproject.vertTerm.Model.Blocker;

/** author Amar Alkhankan **/
public interface BlockerService extends BasicService<Blocker> {
	List<Blocker> getBlockers(String[] id);
	
	/**
	 * @author Joshua Müller
	 */
	boolean exists (String id);
}

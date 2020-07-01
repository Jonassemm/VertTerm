package com.dvproject.vertTerm.Service;

import java.util.List;

import com.dvproject.vertTerm.Model.Blocker;

public interface BlockerService extends BasicService<Blocker> {
	List<Blocker> getBlockers(String[] id);
	
	boolean exists (String id);
}

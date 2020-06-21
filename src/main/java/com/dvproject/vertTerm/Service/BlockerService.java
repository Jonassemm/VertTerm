package com.dvproject.vertTerm.Service;

import java.util.List;

import com.dvproject.vertTerm.Model.Appointment;
import com.dvproject.vertTerm.Model.Blocker;
import com.dvproject.vertTerm.Model.Warning;

public interface BlockerService extends BasicService<Blocker> {
	List<Blocker> getBlockers(String[] id);
}

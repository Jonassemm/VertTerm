package com.dvproject.vertTerm.Service;

import com.dvproject.vertTerm.Model.Restriction;

import java.util.List;


public interface RestrictionService extends BasicService<Restriction> {

	List<Restriction> getRestrictions(String[] ids);
	public boolean testRestrictions(String[] L1,String[] L2);
}


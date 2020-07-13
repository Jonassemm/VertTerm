package com.dvproject.vertTerm.Service;

import com.dvproject.vertTerm.Model.Restriction;

import java.util.List;



/**  author Amar Alkhankan  **/
public interface RestrictionService extends BasicService<Restriction> {

	List<Restriction> getRestrictions(String[] ids);
	public boolean testRestrictions(List<Restriction> L1,List<Restriction> L2) ;
}


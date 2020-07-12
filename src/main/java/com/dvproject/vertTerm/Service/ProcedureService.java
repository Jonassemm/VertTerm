package com.dvproject.vertTerm.Service;

import com.dvproject.vertTerm.Model.*;

import java.util.Date;
import java.util.List;

/**
 * @author Joshua Müller
 */
public interface ProcedureService extends BasicService <Procedure>{
	//GET
	List<Procedure> getAll(Status status);
	
	List<Procedure> getAll(Status status, boolean publicProcedure);
	
	List<Procedure> getByIds(String[] ids);
	
	boolean isAvailableBetween(String id, Date startdate, Date enddate);

}

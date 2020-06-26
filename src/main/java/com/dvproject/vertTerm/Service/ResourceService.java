package com.dvproject.vertTerm.Service;

import com.dvproject.vertTerm.Model.Availability;
import com.dvproject.vertTerm.Model.Resource;
import com.dvproject.vertTerm.Model.Restriction;
import com.dvproject.vertTerm.Model.Status;
import com.dvproject.vertTerm.Model.ResourceType;

import java.util.Date;
import java.util.List;

public interface ResourceService extends BasicService<Resource> {
	// get
	List<Resource> getResources(String[] ids);

	List<Resource> getAll(ResourceType type);

	List<Resource> getResources(Status status);

	List<Restriction> getResourceRestrictions(String ResID);

	List<Availability> getAllAvailabilities(String id);

	List<ResourceType> getResourceTypes(String id);

	List<Resource> getResources(String ResTid);
	
	//update
	List<Availability> updateResourceAvailabilities(String id, List<Availability> availabilities);

	List<Restriction> updateResourceRestrictions(String id, String[] rids);
    
	//test 
	boolean isResourceAvailableBetween(String id, Date startdate, Date enddate);

	
}
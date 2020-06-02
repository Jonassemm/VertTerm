package com.dvproject.vertTerm.Service;
import com.dvproject.vertTerm.Model.Availability;
import com.dvproject.vertTerm.Model.Resource;
import com.dvproject.vertTerm.Model.Restriction;
import com.dvproject.vertTerm.Model.Role;
import com.dvproject.vertTerm.Model.Status;
import com.dvproject.vertTerm.Model.ResourceType;

import java.util.Date;
import java.util.List;


public interface ResourceService extends BasicService<Resource> {

    List<Resource> getResources(String[] ids);
	    
    List<Resource> getResources(Status status);
    
    List<Resource> getResources(String ResTypid);  
    
    
    List<Restriction> getResourceRestrictions (String ResID);
    List<Availability> getResourcevailabilities (String ResID);

    List<Availability> updateResourceAvailabilities(String id, List<Availability> availabilities);
	List<Restriction> updateResourceRestrictions(String id,String[] rids);
	boolean isResourceAvailableBetween(String id, Date startdate, Date enddate);
 
}
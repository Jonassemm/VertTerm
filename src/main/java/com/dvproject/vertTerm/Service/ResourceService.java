package com.dvproject.vertTerm.Service;
import com.dvproject.vertTerm.Model.Resource;
import com.dvproject.vertTerm.Model.Restriction;
import com.dvproject.vertTerm.Model.Role;
import com.dvproject.vertTerm.Model.Status;
import com.dvproject.vertTerm.Model.ResourceType;

import java.util.List;


public interface ResourceService extends BasicService<Resource> {

    List<Resource> getResources(String[] ids);
	    
    List<Resource> getResources(Status status);
    
    List<Resource> getResources(String ResTypid);  
    
    Resource updateResourceAvailabilities(Resource res);
    
    List<Restriction> getResourceRestrictions (String ResID);
   
	List<Restriction> updateResourceRestrictions(String id,String[] rids);

 
}
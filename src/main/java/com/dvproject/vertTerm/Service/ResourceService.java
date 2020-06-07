package com.dvproject.vertTerm.Service;
import com.dvproject.vertTerm.Model.Availability;
import com.dvproject.vertTerm.Model.Resource;
import com.dvproject.vertTerm.Model.Restriction;
import com.dvproject.vertTerm.Model.Role;
import com.dvproject.vertTerm.Model.Status;
import com.dvproject.vertTerm.Model.ResourceType;
import com.dvproject.vertTerm.Model.*;

import java.util.Date;
import java.util.List;


public interface ResourceService extends BasicService<Resource> {

    List<Resource> getResources(String[] ids);

    List<Resource> getAll(ResourceType type);

    List<Resource> getResources(Status status);

    List<Restriction> getResourceRestrictions (String ResID);

    List<Availability> updateResourceAvailabilities(String id, List<Availability> availabilities);
    List<Restriction> updateResourceRestrictions(String id,String[] rids);
    boolean isResourceAvailableBetween(String id, Date startdate, Date enddate);

    List<ResourceType> getResourceTypes(String id);
    List<Resource> getResources(String ResTid);

}
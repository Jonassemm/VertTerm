package com.dvproject.vertTerm.Service;

import java.util.List;

import com.dvproject.vertTerm.Model.ResourceType;


public interface ResourceTypeService extends BasicService<ResourceType> {
	

    List<ResourceType> getResourceTypes(String[] ids);
 
 
}


package com.dvproject.vertTerm.Service;

import java.util.List;

import com.dvproject.vertTerm.Model.ResourceType;


public interface ResourceTypeService {
	
	ResourceType createResourceType(ResourceType restype);

	ResourceType updateResourceType(ResourceType restype);

    List<ResourceType> getAllResourceType();

    List<ResourceType> getResourceTypes(String[] ids);

    ResourceType getResourceTypeById(String id);

    void deleteResourceTypeById(String id);
    
 
}


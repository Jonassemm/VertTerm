package com.dvproject.vertTerm.Service;

import java.util.List;

import com.dvproject.vertTerm.Model.ResourceType;
import com.dvproject.vertTerm.Model.Status;

//author Amar Alkhankan
public interface ResourceTypeService extends BasicService<ResourceType> {
	

    List<ResourceType> getResourceTypes(String[] ids);
 
    List<ResourceType> getAll(Status status);
}


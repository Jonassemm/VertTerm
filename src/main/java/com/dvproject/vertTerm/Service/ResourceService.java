package com.dvproject.vertTerm.Service;

import java.util.List;

import com.dvproject.vertTerm.Model.Employee;
import com.dvproject.vertTerm.Model.Resource;

public interface ResourceService {
	
	Resource createResource(Resource res);
	Resource updateResource(Resource res);

//	Resource updateResourceAvailability(Resource res);
//	
//	Resource updateEmployeePermission(String resID,String empID,String roleID);
	
    List<Resource> getAllResources();
    
    List<Employee> getAllResourceEmps();

    Resource getResourceById(String id);

    void deleteResourceById(String id);
    
//    void updateResourceDependencies(String ResID ,String ParResID ,List<String> ChildResIDs);
//
//    void getResourceDependencies (String ResID);
    
    // createResourceFailure 
    // updateResourceFailure 
}
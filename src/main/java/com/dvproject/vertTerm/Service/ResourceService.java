package com.dvproject.vertTerm.Service;

import com.dvproject.vertTerm.Model.Employee;
import com.dvproject.vertTerm.Model.Resource;

import java.util.List;

public interface ResourceService extends Service<Resource> {

//	Resource updateResourceAvailability(Resource res);
//	
//	Resource updateEmployeePermission(String resID,String empID,String roleID);
    
    List<Employee> getAllResourceEmps();
    
//    void updateResourceDependencies(String ResID ,String ParResID ,List<String> ChildResIDs);
//
//    void getResourceDependencies (String ResID);
    
    // createResourceFailure 
    // updateResourceFailure 
}
package com.dvproject.vertTerm.Service;

import com.dvproject.vertTerm.Model.Employee;
import com.dvproject.vertTerm.Model.Resource;
import com.dvproject.vertTerm.Model.Restriction;
import com.dvproject.vertTerm.Model.Role;

public interface ResourceService {
	
	
    List<Resource> getAllResources();
    
    Resource getResourceById(String id);

import java.util.List;

public interface ResourceService extends BasicService<Resource> {

//	Resource updateResourceAvailability(Resource res);
//	
//	Resource updateEmployeePermission(String resID,String empID,String roleID);
    
    List<Employee> getAllResourceEmps();
    
    List<Resource> getResources(String[] ids);
    
    Resource createResource(Resource res);
	
    Resource updateResource(Resource res);
    
    boolean deleteResourceById(String id);
    
    Resource updateResourceAvailability(Resource res);
    
   
    List<Restriction> getResourceDependencies (String ResID);
   
    Resource updateResourceDependencies(Resource res);
    
    //TODO
    // 
    // List<Employee> getAllResourceEmps();
    // Resource updateEmployeePermission(String resID,String empID,String roleID);
    // createResourceFailure 
    // updateResourceFailure 
}
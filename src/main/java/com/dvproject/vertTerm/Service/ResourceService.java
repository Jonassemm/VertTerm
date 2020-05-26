package com.dvproject.vertTerm.Service;
import com.dvproject.vertTerm.Model.Employee;
import com.dvproject.vertTerm.Model.Resource;
import com.dvproject.vertTerm.Model.Restriction;
import com.dvproject.vertTerm.Model.Role;

import java.util.List;


public interface ResourceService extends BasicService<Resource> {

    List<Resource> getResources(String[] ids);
	    
    Resource updateResourceAvailability(Resource res);
    
    List<Restriction> getResourceDependencies (String ResID);
   
	List<Restriction> updateResourceDependencies(String id,String[] rids);

    //TODO
    // 
    // List<Employee> getAllResourceEmps();
    // Resource updateEmployeePermission(String resID,String empID,String roleID);
    // Ressourcenangestelltenrollen bearbeiten 
    // createResourceFailure 
    // updateResourceFailure 
	// Ressourcenzeiten anzeigen
}
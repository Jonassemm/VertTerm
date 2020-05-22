package com.dvproject.vertTerm.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.dvproject.vertTerm.Model.Resource;
import com.dvproject.vertTerm.Model.Restriction;
import com.dvproject.vertTerm.Model.Status;
import com.dvproject.vertTerm.repository.RessourceRepository;

@Service
public class ResourceServiceImp implements ResourceService {

		
	@Autowired
	private RessourceRepository ResRepo;
	

	//@PreAuthorize("hasAuthority('RESOURCE_DATA_WRITE')")
	public Resource createResource (Resource res) {
	      if (this.ResRepo.findByName(res.getName()) == null)
	   	     return ResRepo.save(res);
	        if (ResRepo.findById(res.getId()).isPresent()) {
		   		throw new ResourceNotFoundException("Resource with the given id :" + res.getId() + " already exists");
	        }
			return null;
	}
	
	
	//@PreAuthorize("hasAuthority('RESOURCE_DATA_READ')")
	public List<Resource> getAllResources() {
		 return this.ResRepo.findAll();
	}


	//@PreAuthorize("hasAuthority('RESOURCE_DATA_READ')")
	public Resource getResourceById(String id) {
		  Optional<Resource> ResDb = this.ResRepo.findById(id);
	       if (ResDb.isPresent()) {
	           return ResDb.get();
	       } else {
	    		throw new ResourceNotFoundException("Resource with the given id :" +id + " already exists");
	      		
	       }
	}
	
	//@PreAuthorize("hasAuthority('RESOURCE_DATA_READ')")
	public List<Resource> getResources(String[] ids) {
	   	List<Resource> Resources = new ArrayList<Resource> ();

	   	for (String id : ids) {
	   		Resources.add(this.getResourceById(id));
	   	}

	   	return Resources;
	}
	
	
	//@PreAuthorize("hasAuthority('RESOURCE_DATA_READ')")
	public List<Restriction> getResourceDependencies(String id) {
		  List<Restriction> dep = new ArrayList<Restriction>();
		  Resource res = getResourceById(id);
		  for (Restriction rest : res.getRestrictions()) 
		  { 
			  if (!dep.contains(rest)) 
			     dep.add(rest);					          
		  }
		  return dep; 
	       
    }
	
	//@PreAuthorize("hasAuthority('RESOURCE_DATA_WRITE')")
	public Resource updateResource(Resource res) {
		  Optional<Resource> ResDb = this.ResRepo.findById(res.getId());
		    if (ResDb.isPresent()) {
		    	Resource ResUpdate = ResDb.get();
		    	ResUpdate.setId(res.getId());
		    	ResUpdate.setName(res.getName());
		    	ResUpdate.setDescription(res.getDescription());
		    	ResUpdate.setAvailability(res.getAvailability());
		    	ResUpdate.setAvailabilities(res.getAvailabilities());
		    	ResUpdate.setChildRessources(res.getChildRessources());
		    	ResUpdate.setResourceTyp(res.getResourceTyp());
		    	ResUpdate.setStatus(res.getStatus());
		    	ResUpdate.setRestrictions(res.getRestrictions());
		        ResRepo.save(ResUpdate);
		        return ResUpdate;
		    } 
		    else {
		    	throw new ResourceNotFoundException("Resource with the given id :" +res.getId() + " already exists");  
		    } 
	}
	
	//@PreAuthorize("hasAuthority('RESOURCE_STATUS_WRITE')")
	public boolean deleteResourceById(String id) {
	    Resource Res = getResourceById(id);
		return Res.getStatus() == Status.DELETED;
	}
	
	
	//@PreAuthorize("hasAuthority('RESOURCE_AVAILABILITIES_WRITE')")
		public Resource updateResourceAvailability(Resource res) {
			  Optional<Resource> ResDb = this.ResRepo.findById(res.getId());
			    if (ResDb.isPresent()) {
			    	Resource ResUpdate = ResDb.get();
			    	ResUpdate.setAvailability(res.getAvailability());
			        ResRepo.save(ResUpdate);
			        return ResUpdate;
			    } 
			    else {
			    	throw new ResourceNotFoundException("Resource with the given id :" + res.getId() + " already exists");
			      	
			    }    
	     } 
		
		//@PreAuthorize("hasAuthority('RESOURCE_DATA_WRITE')")
		public Resource updateResourceDependencies(Resource res) {
			  Optional<Resource> ResDb = this.ResRepo.findById(res.getId());
			    if (ResDb.isPresent()) {
			    	Resource ResUpdate = ResDb.get();
			    	ResUpdate.setRestrictions(res.getRestrictions());
			        ResRepo.save(ResUpdate);
			        return ResUpdate;
			    } 
			    else {
			    	throw new ResourceNotFoundException("Resource with the given id :" +res.getId() + " already exists");
			      	      
			    }
	     } 

		
//		@Override
//		public List<Employee> getAllResourceEmps() {
//			// TODO Auto-generated method stub
//			return null;
//		}



//	@Override
//	public Resource updateEmployeePermission(String resID, String empID, String roleID) {
//	       // TODO Auto-generated method stub
//	return null;
//	}
  
}


//RESOURCE_INHERITANCE_READ
//RESOURCE_INHERITANCE_WRITE
//RESOURCE_STATUS_READ
//RESOURCE_AVAILABILITIES_READ
//RESOURCE_TYPE_READ
//RESOURCE_TYPE_WRITE

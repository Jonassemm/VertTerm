package com.dvproject.vertTerm.Service;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.dvproject.vertTerm.Model.Resource;
import com.dvproject.vertTerm.Model.ResourceType;
import com.dvproject.vertTerm.Model.Restriction;
import com.dvproject.vertTerm.Model.Right;
import com.dvproject.vertTerm.Model.Status;
import com.dvproject.vertTerm.repository.RessourceRepository;
import com.dvproject.vertTerm.repository.RestrictionRepository;


@Service
public class ResourceServiceImp implements ResourceService {

		
	@Autowired
	private RessourceRepository ResRepo;
	@Autowired
	private RestrictionRepository RestsRepo;
	

		//@PreAuthorize("hasAuthority('RESOURCE_DATA_READ')")
		public List<Resource> getAll() {
			 return this.ResRepo.findAll();
		}


		//@PreAuthorize("hasAuthority('RESOURCE_DATA_READ')")
		public Resource getById(String id) {
			  Optional<Resource> ResDb = this.ResRepo.findById(id);
		       if (ResDb.isPresent()) 
		           return ResDb.get();
		       else 
		    	   throw new ResourceNotFoundException("Resource with the given id :" + id + " already exists");		      
        }

		//@PreAuthorize("hasAuthority('RESOURCE_DATA_WRITE')")
		   public Resource create(Resource res) {
			      if(this.ResRepo.findByName(res.getName()) == null)  
			   	   return ResRepo.save(res);
			         else 
			        throw new ResourceNotFoundException("Resource with the given id :" + res.getId() + " already exists");
			       
		   }

		
		//@PreAuthorize("hasAuthority('RESOURCE_DATA_WRITE')")
		public Resource update(Resource res) {
			if (res.getId() != null && ResRepo.findById(res.getId()).isPresent()) {
				
				return ResRepo.save(res);
			}
		  
			    else {
			    	throw new ResourceNotFoundException("Resource with the given id :" +res.getId() + "not found");  
			    } 
		}
		
		//@PreAuthorize("hasAuthority('RESOURCE_STATUS_WRITE')")
		public boolean delete(String id) {
		    Resource Res = getById(id);
			Res.setStatus(Status.DELETED);
			ResRepo.save(Res);
			return Res.getStatus() == Status.DELETED;	
		}

		
		//@PreAuthorize("hasAuthority('RESOURCE_DATA_READ')")
		public List<Resource> getResources(String[] ids) {
		   	List<Resource> Resources = new ArrayList<Resource> ();	
		   	for (String id : ids) {
		   		Resources.add(this.getById(id));
		   	}
		   	return Resources;
		}
		
		//@PreAuthorize("hasAuthority('RESOURCE_DATA_READ')")
		public List<Restriction> getResourceRestrictions(String id) {
			  List<Restriction> dep = new ArrayList<Restriction>();
			  Resource res = getById(id);
			  for (Restriction rest : res.getRestrictions()) 
			  { 
				  if (!dep.contains(rest)) 
				     dep.add(rest);					          
			  }
			  return dep; 
		       
	    }
	
	//@PreAuthorize("hasAuthority('RESOURCE_AVAILABILITIES_WRITE')")
		public Resource updateResourceAvailabilities(Resource res) {
			  Optional<Resource> ResDb = this.ResRepo.findById(res.getId());
			    if (ResDb.isPresent()) {
			    	Resource ResUpdate = ResDb.get();
			    	ResUpdate.setAvailabilities(res.getAvailabilities());
			        ResRepo.save(ResUpdate);
			        return ResUpdate;
			    } 
			    else {
			    	throw new ResourceNotFoundException("Resource with the given id :" + res.getId() + "not found");  
			      	
			    }    
	     } 
		
		//@PreAuthorize("hasAuthority('RESOURCE_DATA_WRITE')")
		public List<Restriction> updateResourceRestrictions(String id,String[] Rids) {
			 List<Restriction> dep = new ArrayList<Restriction>();
			 List<Restriction> AllRestrictions = RestsRepo.findAll();
			 List<String> RestIds = List.of(Rids);				
			 for (Restriction rest : AllRestrictions) 
			  {   String rid=rest.getId();
				 if (RestIds.contains(rid)) 
			   		{
					  if (!dep.contains(rest)) 
						     dep.add(rest);	
			   		}
								          
			  }			 
			    Optional<Resource> ResDb = this.ResRepo.findById(id);
			    if (ResDb.isPresent()) {
			    	Resource ResUpdate = ResDb.get();
			    	ResUpdate.setRestrictions(dep);
			        ResRepo.save(ResUpdate);
			        return dep;
			    } 
			    else {
			    	throw new ResourceNotFoundException("Resource with the given id :" + id + "not found");  
			      	      
			    }
	     }


		@Override
		public List<Resource> getResources(Status status) {
			return ResRepo.findByStatus(status);
		}


		@Override
		public List<Resource> getResources(String ResTid) {
			 List<Resource> Resources = new ArrayList<> ();
			 List<Resource> AllResources = ResRepo.findAll();
			 for (Resource r : AllResources)
			 {   
				 ResourceType rt=r.getResourceTyp();
				 if (rt.getId().equals(ResTid))
			   		{
					 if (!Resources.contains(r)) 
						 Resources.add(r);	
			   		}
			 }
			 return Resources;
		}

}



package com.dvproject.vertTerm.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dvproject.vertTerm.Model.ResourceType;
import com.dvproject.vertTerm.Service.ResourceTypeService;
import com.dvproject.vertTerm.repository.ResourceTypeRepository;



@Service
@Transactional
public class ResourceTypeServiceImp implements ResourceTypeService {

	
   @Autowired
   private ResourceTypeRepository ResourceTypeRepo;

	//@PreAuthorize("hasAuthority('RESOURCETYPE_DATA_READ')")
   public List<ResourceType> getAll() {
   	  return this.ResourceTypeRepo.findAll();
   }
   
	//@PreAuthorize("hasAuthority('RESOURCETYPE_DATA_READ')")
	public ResourceType getById(String id) {
		  Optional <ResourceType> ResTypDb = this.ResourceTypeRepo.findById(id);
		    if (ResTypDb.isPresent()) {
		        return ResTypDb.get();
		    } else {
		    	throw new ResourceNotFoundException("Resource with the given id :" +id + " already exists");
		    }
	
	}
	   

	//@PreAuthorize("hasAuthority('RESOURCETYPE_DATA_WRITE')")
   public ResourceType create(ResourceType restype) {
	     if(this.ResourceTypeRepo.findByName(restype.getName()) == null) {
	    	 restype.setName(capitalize(restype.getName()));  
	  	   return ResourceTypeRepo.save(restype);
	  	   }
	       if (ResourceTypeRepo.findById(restype.getId()).isPresent()) {
	   		throw new ResourceNotFoundException("ResourceType with the given id :" + restype.getId() + " already exists");
	  		}
   		return null;
	    
}

    //@PreAuthorize("hasAuthority('RESOURCETYPE_DATA_WRITE')")
    public ResourceType update(ResourceType restype) {
		if (restype.getId() != null && ResourceTypeRepo.findById(restype.getId()).isPresent()) {
			 restype.setName(capitalize(restype.getName()));  
			return ResourceTypeRepo.save(restype);
		}
	    else {
	        throw new ResourceNotFoundException("ResourceType with the given id : " + restype.getId()  + " not found ");
	    } 
}


	//@PreAuthorize("hasAuthority('RESOURCETYPE_DATA_WRITE')")
	public boolean delete(String id) {
	  Optional <ResourceType> ResTypDb = this.ResourceTypeRepo.findById(id);

      if (ResTypDb.isPresent()) {
    	    this.ResourceTypeRepo.delete(ResTypDb.get());
      } else {
          throw new ResourceNotFoundException("ResourceType with the given id : " + id + " not found ");
      }
      return ResourceTypeRepo.existsById(id);
	}

	//@PreAuthorize("hasAuthority('RESOURCETYPE_DATA_READ')")
	   public List<ResourceType> getResourceTypes(String[] ids) {
	   	List<ResourceType> ResTypes = new ArrayList<ResourceType> ();

	   	for (String id : ids) {
	   		ResTypes.add(this.getById(id));
	   	}

	   	return ResTypes;
	   }
	  
		  public static String capitalize(String str)
		    {
		        if(str == null) return str;
		        return str.toUpperCase() ;
		        
		    }


}
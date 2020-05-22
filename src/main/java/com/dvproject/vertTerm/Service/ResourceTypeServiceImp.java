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
   public List<ResourceType> getAllResourceType() {
   	  return this.ResourceTypeRepo.findAll();
   }
   
	//@PreAuthorize("hasAuthority('RESOURCETYPE_DATA_READ')")
   public ResourceType getResourceTypeById(String id) {
     Optional <ResourceType> ResTypDb = this.ResourceTypeRepo.findById(id);
    if (ResTypDb.isPresent()) {
        return ResTypDb.get();
    } else {
        throw new ResourceNotFoundException("Record not found with id : " + id);
    }
   }
   
	//@PreAuthorize("hasAuthority('RESOURCETYPE_DATA_READ')")
   public List<ResourceType> getResourceTypes(String[] ids) {
   	List<ResourceType> ResTypes = new ArrayList<ResourceType> ();

   	for (String id : ids) {
   		ResTypes.add(this.getResourceTypeById(id));
   	}

   	return ResTypes;
   }
   
	//@PreAuthorize("hasAuthority('RESOURCETYPE_DATA_WRITE')")
   public ResourceType createResourceType(ResourceType restype) {
	     if(this.ResourceTypeRepo.findByName(restype.getName()) == null)
	  	   return ResourceTypeRepo.save(restype);
	       if (ResourceTypeRepo.findById(restype.getId()).isPresent()) {
	   		throw new ResourceNotFoundException("ResourceType with the given id :" + restype.getId() + " already exists");
	  		}
   		return null;
	    
}

//@PreAuthorize("hasAuthority('RESOURCETYPE_DATA_WRITE')")
public ResourceType updateResourceType(ResourceType restype) {
	   Optional <ResourceType> ResTypDb = this.ResourceTypeRepo.findById(restype.getId());
	    if (ResTypDb.isPresent()) {
	    	ResourceType ResTypUpdate = ResTypDb.get();
	        ResTypUpdate.setId(restype.getId());
	        ResTypUpdate.setName(restype.getName());
	        ResTypUpdate.setDescription(restype.getDescription());
	        ResourceTypeRepo.save(ResTypUpdate);
	        return ResTypUpdate;
	    } 
	    else {
	        throw new ResourceNotFoundException("ResourceType with the given id : " + restype.getId()  + " not found ");
	    } 
}



	//@PreAuthorize("hasAuthority('RESOURCETYPE_DATA_WRITE')")
	public void deleteResourceTypeById(String id) {
	  Optional <ResourceType> ResTypDb = this.ResourceTypeRepo.findById(id);

      if (ResTypDb.isPresent()) {
          this.ResourceTypeRepo.delete(ResTypDb.get());
      } else {
          throw new ResourceNotFoundException("ResourceType with the given id : " + id + " not found ");
      }

}


}
package com.dvproject.vertTerm.Service;

import com.dvproject.vertTerm.Model.Employee;
import com.dvproject.vertTerm.Model.Resource;
import com.dvproject.vertTerm.repository.RessourceRepository;
import net.springboot.javaguides.exception.ResourceExsistException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ResourceServiceImp implements ResourceService {

	@Autowired
	private RessourceRepository ResRepo;
	


	//@PreAuthorize("hasAuthority('RESOURCE_DATA_WRITE')")
	public Resource create(Resource res) {
	      if(this.ResRepo.findByName(res.getName()).isEmpty())
	   	   return ResRepo.save(res);
	         else 
	           throw new ResourceExsistException("Record already exists");
	}
	
	
	//@PreAuthorize("hasAuthority('RESOURCE_DATA_READ')")
	public List<Resource> getAll() {
		 return this.ResRepo.findAll();
	}


	//@PreAuthorize("hasAuthority('RESOURCE_DATA_READ')")
	public Resource getById(String id) {
		  Optional<Resource> ResDb = this.ResRepo.findById(id);
	       if (ResDb.isPresent()) {
	           return ResDb.get();
	       } else {
	           throw new ResourceNotFoundException("Record not found with id : " + id);
	       }
	}

	//@PreAuthorize("hasAuthority('RESOURCE_WRITE')")
	public Resource update(Resource res) {
		  Optional<Resource> ResDb = this.ResRepo.findById(res.getId());
		    if (ResDb.isPresent()) {
		    	Resource ResUpdate = ResDb.get();
		    	ResUpdate.setId(res.getId());
		    	ResUpdate.setName(res.getName());
		    	ResUpdate.setDescription(res.getDescription());
		    	ResUpdate.setAvailability(res.getAvailability());
		    	ResUpdate.setChildRessources(res.getChildRessources());
		        ResRepo.save(ResUpdate);
		        return ResUpdate;
		    } 
		    else {
		        throw new ResourceNotFoundException("Record not found with id : " + res.getId());
			     
		    } 
		
	}
	
	@Override
	public boolean delete(String id) {
	   Optional<Resource> ResDb = this.ResRepo.findById(id);
	       if (ResDb.isPresent()) {
	           this.ResRepo.delete(ResDb.get());
	       } else {
	           throw new ResourceNotFoundException("Record not found with id : " + id);
	       }
		return ResRepo.existsById(id);
		
	}
	
	@Override
	public List<Employee> getAllResourceEmps() {
		// TODO Auto-generated method stub
		return null;
	}
	
//	@Override
//	public void updateResourceDependencies(String ResID, String ParResID, List<String> ChildResIDs) {
//		// TODO Auto-generated method stub
//		
//	}
//	
//	@PreAuthorize("hasAuthority('RESOURCE_AVAILABILITIES_WRITE')")
//	public Resource updateResourceAvailability(Resource res) {
//	//TODO
//		
//		return null;
//	}
//	
//	@Override
//	public void getResourceDependencies(String ResID) {
//			// TODO Auto-generated method stub
//			
//    }
	
//	@Override
//	public Resource updateEmployeePermission(String resID, String empID, String roleID) {
//	       // TODO Auto-generated method stub
//	return null;
//	}


    
}



  






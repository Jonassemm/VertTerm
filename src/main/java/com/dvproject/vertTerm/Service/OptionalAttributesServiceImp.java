package com.dvproject.vertTerm.Service;

import com.dvproject.vertTerm.Model.OptionalAttribute;
import com.dvproject.vertTerm.Model.OptionalAttributes;
import com.dvproject.vertTerm.Model.Resource;
import com.dvproject.vertTerm.Model.Restriction;
import com.dvproject.vertTerm.Model.Role;
import com.dvproject.vertTerm.Model.User;
import com.dvproject.vertTerm.repository.OptionalAttributesRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
public class OptionalAttributesServiceImp implements OptionalAttributesService {

   @Autowired
   private OptionalAttributesRepository OptionalAttributesRepo;
@Override
public List<OptionalAttributes> getAll() {
	return OptionalAttributesRepo.findAll();
}

@Override
public OptionalAttributes getById(String id) {
Optional<OptionalAttributes> OptionalAttributes = OptionalAttributesRepo.findById(id);
	
	if (OptionalAttributes.isPresent()) {
		return OptionalAttributes.get();
	} else {
		throw new ResourceNotFoundException("OptionalAttributes with the given id :" + id + " not found");
	}
	
}

@Override
public OptionalAttributes create(OptionalAttributes OAttribute) {
   if(this.OptionalAttributesRepo.findByClass(OAttribute.getClassOfOptionalAttribut()) == null)  
	   return OptionalAttributesRepo.save(OAttribute);
   else
		throw new ResourceNotFoundException("OptionalAttributes  with the given Class :" +OAttribute.getClass() + "already exsist");   
}


@Override
public OptionalAttributes update(OptionalAttributes OAttribute) {
	Optional<OptionalAttributes> OptionalAttributes = OptionalAttributesRepo.findById(OAttribute.getId());
	if (OptionalAttributes.isPresent()) {
		return OptionalAttributesRepo.save(OAttribute);
	}
	else	
		throw new ResourceNotFoundException("OptionalAttributes with the given id :" + OAttribute.getId() + " not found");
		
}


	@Override
	public boolean delete(String id) {
	Optional<OptionalAttributes> OptionalAttributes = OptionalAttributesRepo.findById(id);
		if (OptionalAttributes.isPresent()) {
			this.OptionalAttributesRepo.delete(OptionalAttributes.get());
		} else {
			throw new ResourceNotFoundException("OptionalAttributes with the given id :" + id + " not found");
		}
	    return false;
		
	}
@Override
	public List<OptionalAttribute> addOptionalAttribute(String id, OptionalAttribute OAtt) {
	   Optional<OptionalAttributes> OAsDb = this.OptionalAttributesRepo.findById(id);
	   List<OptionalAttribute> OptionalAttributList = new ArrayList<> ();	
	   if (OAsDb.isPresent()) {
	      	OptionalAttributes oasUpdate = OAsDb.get();
	    	 List<OptionalAttribute> OAlist = oasUpdate.getOptionalAttributes();
	    	 List<String> names = new ArrayList<> ();
	    	// if (!OAlist.contains(OAtt)) 
	    	 OAlist.add(OAtt);
			 		
	    	 for (OptionalAttribute oa : OAlist )
			  {    	 String oaname=oa.getName();
				     if (!(names.contains(oaname)) )
	    			 {  
	    				 	names.add(oa.getName());
	    					 if (!OptionalAttributList.contains(oa)) 
	 	    		        	OptionalAttributList.add(oa);	
	    			 }	
				     else 
				 		throw new ResourceNotFoundException("OptionalAttribute  with the given name : "+OAtt.getName()+" already exsist");   		          
	    			 
			  }
	    	oasUpdate.setOptionalAttributes(OptionalAttributList);
	    	OptionalAttributesRepo.save(oasUpdate);
	        return OptionalAttributList;
	    } 
        else
		throw new ResourceNotFoundException("OptionalAttributes  with the given id : "+id +"not found");   
}

	@Override
	public List<OptionalAttribute> updateOptionalAttribute(String id, List<OptionalAttribute> opatt) {
		Optional<OptionalAttributes> OptionalAttributes = OptionalAttributesRepo.findById(id);	
		if (OptionalAttributes.isPresent()) {
			OptionalAttributes OAsUpdate = OptionalAttributes.get();
			OAsUpdate.setOptionalAttributes(opatt);
		    OptionalAttributesRepo.save(OAsUpdate);
			return OAsUpdate.getOptionalAttributes();
		} 
		else	
			throw new ResourceNotFoundException("OptionalAttributes with the given id :" + id + " not found");
		
	}

	@Override
	public List<OptionalAttribute> deleteOptionalAttribute(String id, OptionalAttribute OAtt) { 
	   Optional<OptionalAttributes> OAsDb = this.OptionalAttributesRepo.findById(id);
	   List<OptionalAttribute> OptionalAttributList = new ArrayList<> ();	
	   if (OAsDb.isPresent()) {
	         OptionalAttributes oasUpdate = OAsDb.get();
	    	 List<OptionalAttribute> OAlist = oasUpdate.getOptionalAttributes();
	    	 List<String> names = new ArrayList<> ();
	    	 String RemEml=OAtt.getName();
	    	 for (OptionalAttribute oa : OAlist )
			  {    	
    		     String oaname=oa.getName();
	    		 if (!(names.contains(oaname)) )
    			 {  
    				 	names.add(oa.getName());
    			 }
	    		 if (names.contains(RemEml)) 
    			 {
			         if(!(RemEml.equals(oaname)))
			         {    	 if (!OptionalAttributList.contains(oa)) 
		 	    		        	OptionalAttributList.add(oa);		
			         }
			      }
	    		 else throw new ResourceNotFoundException("OptionalAttributes with the name : "+ RemEml + " not exsist");   
	    			
			  }
	    	oasUpdate.setOptionalAttributes(OptionalAttributList);
	    	OptionalAttributesRepo.save(oasUpdate);
	        return OptionalAttributList;
	    } 
     else
		throw new ResourceNotFoundException("OptionalAttributes with the given id : "+id +"not found");   
	}

	
	public List<OptionalAttributes> getOptionalAttributeswithIDS(String[] ids) {
		   	List<OptionalAttributes> OpAttsList = new ArrayList<> ();

		   	for (String id : ids) {
		   		OpAttsList.add(this.getById(id));
		   	}

		   	return OpAttsList;
		
	}
	
	public List<OptionalAttribute> getOptionalAttributes(String id) {
		List<OptionalAttribute> OA = new ArrayList<>();
		OptionalAttributes oas = getById(id);
		for (OptionalAttribute rest : oas.getOptionalAttributes()) {
			if (!OA.contains(rest))
				OA.add(rest);
		}
		return OA;

	}
	public OptionalAttributes getByClassname(String classname) {
		return OptionalAttributesRepo.findByClass(classname);
	}

	@Override
	public void testMandatoryFields(String classname, List<OptionalAttribute> optionalAttributes) {
		getByClassname(classname).testMandatoryFields(optionalAttributes);
	}


}


 




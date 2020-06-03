package com.dvproject.vertTerm.Service;

import com.dvproject.vertTerm.Model.OptionalAttributes;
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

	

}


 




package com.dvproject.vertTerm.Service;

import com.dvproject.vertTerm.Model.OptionalAttribute;
import com.dvproject.vertTerm.Model.OptionalAttributes;
import com.dvproject.vertTerm.Model.Restriction;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.Query;


public interface OptionalAttributesService extends BasicService<OptionalAttributes> {

	List<OptionalAttribute> AddOptionalAtt(String id, OptionalAttribute opatt);
  
	OptionalAttributes getByClassname(String classname);
	
	void testMandatoryFields(String classname, List<OptionalAttribute> optionalAttributes);
 

}


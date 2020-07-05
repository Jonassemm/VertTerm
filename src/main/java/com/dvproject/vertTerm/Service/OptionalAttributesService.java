package com.dvproject.vertTerm.Service;

import com.dvproject.vertTerm.Model.OptionalAttribute;
import com.dvproject.vertTerm.Model.OptionalAttributes;
import com.dvproject.vertTerm.Model.Restriction;
import com.dvproject.vertTerm.Model.Role;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.Query;

//author Amar Alkhankan
public interface OptionalAttributesService extends BasicService<OptionalAttributes> {

	List<OptionalAttribute> addOptionalAttribute(String id, OptionalAttribute opatt);
	List<OptionalAttribute> updateOptionalAttribute(String id, List<OptionalAttribute> opatt);
	List<OptionalAttribute> deleteOptionalAttribute(String id, OptionalAttribute opatt);  
	
	List<OptionalAttributes> getOptionalAttributeswithIDS(String[] ids);
	
	List<OptionalAttribute> getOptionalAttributes(String id);
	
	
	OptionalAttributes getByClassname(String classname);
	void testMandatoryFields(String classname, List<OptionalAttribute> optionalAttributes);

}


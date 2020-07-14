package com.dvproject.vertTerm.Service;

import com.dvproject.vertTerm.Model.OptionalAttribute;
import com.dvproject.vertTerm.Model.OptionalAttributes;

import java.util.List;

/**  author Amar Alkhankan  **/

public interface OptionalAttributesService extends BasicService<OptionalAttributes> {

	List<OptionalAttribute> addOptionalAttribute(String id, OptionalAttribute opatt);
	List<OptionalAttribute> updateOptionalAttribute(String id, List<OptionalAttribute> opatt);
	List<OptionalAttribute> deleteOptionalAttribute(String id, OptionalAttribute opatt);  
	
	List<OptionalAttributes> getOptionalAttributeswithIDS(String[] ids);
	
	List<OptionalAttribute> getOptionalAttributes(String id);
	
	
	/**
	 * @author Joshua Müller
	 */
	OptionalAttributes getByClassname(String classname);
	void testMandatoryFields(String classname, List<OptionalAttribute> optionalAttributes);

}


package com.dvproject.vertTerm.Service;

import com.dvproject.vertTerm.Model.OptionalAttribute;
import com.dvproject.vertTerm.Model.OptionalAttributes;
import com.dvproject.vertTerm.repository.OptionalAttributesRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**  author Amar Alkhankan  **/

@Service
@Transactional
public class OptionalAttributesServiceImp implements OptionalAttributesService {

	@Autowired
	private OptionalAttributesRepository OptionalAttributesRepo;

	// @PreAuthorize("hasAuthority('')")
	public List<OptionalAttributes> getAll() {
		// get a list of all OptionalAttributes from DB
		return OptionalAttributesRepo.findAll();
	}

	// @PreAuthorize("hasAuthority('')")
	public OptionalAttributes getById(String id) {
		// get an OptionalAttributes by the OptionalAttributes-ID
		Optional<OptionalAttributes> OptionalAttributes = OptionalAttributesRepo.findById(id);
		if (OptionalAttributes.isPresent()) {
			return OptionalAttributes.get();
		} else {
			throw new ResourceNotFoundException("OptionalAttributes with the given id :" + id + " not found");
		}

	}

	// @PreAuthorize("hasAuthority('USER_WRITE')")
	public OptionalAttributes create(OptionalAttributes OAttribute) {
		// get a new OptionalAttributes if not exist
		List<OptionalAttribute> OpAttList = new ArrayList<>();
		if (this.OptionalAttributesRepo.findByClass(OAttribute.getClassOfOptionalAttribut()) == null) {
			for (OptionalAttribute oa : OAttribute.getOptionalAttributes()) {
				oa.setName(capitalize(oa.getName()));
				OpAttList.add(oa);
			}
			OAttribute.setOptionalAttributes(OpAttList);
			return OptionalAttributesRepo.save(OAttribute);

		} else
			throw new ResourceNotFoundException(
					"OptionalAttributes  with the given Class :" + OAttribute.getClass() + "already exsist");
	}

	// @PreAuthorize("hasAuthority('USER_WRITE')")
	public OptionalAttributes update(OptionalAttributes OAttribute) {
		// update an OptionalAttributes if it's exist
		Optional<OptionalAttributes> OptionalAttributes = OptionalAttributesRepo.findById(OAttribute.getId());
		if (OptionalAttributes.isPresent()) {
			return OptionalAttributesRepo.save(OAttribute);
		} else
			throw new ResourceNotFoundException(
					"OptionalAttributes with the given id :" + OAttribute.getId() + " not found");

	}

	@Override
	public boolean delete(String id) {
		// delete an OptionalAttributes from DB
		Optional<OptionalAttributes> OptionalAttributes = OptionalAttributesRepo.findById(id);
		if (OptionalAttributes.isPresent()) {
			this.OptionalAttributesRepo.delete(OptionalAttributes.get());
		} else {
			throw new ResourceNotFoundException("OptionalAttributes with the given id :" + id + " not found");
		}
		return false;

	}

	// @PreAuthorize("hasAuthority('USER_WRITE')")
	public List<OptionalAttribute> addOptionalAttribute(String id, OptionalAttribute OAtt) {
		// add an OptionalAttribute to the OptionalAttributes-List of the
		// OptionalAttributes-Object with the given ID
		Optional<OptionalAttributes> OAsDb = this.OptionalAttributesRepo.findById(id);
		List<OptionalAttribute> OptionalAttributeList = new ArrayList<>();
		if (OAsDb.isPresent()) {
			OptionalAttributes oasUpdate = OAsDb.get();
			OptionalAttributeList = Add(oasUpdate, OAtt);
			oasUpdate.setOptionalAttributes(OptionalAttributeList);
			OptionalAttributesRepo.save(oasUpdate);
			return OptionalAttributeList;
		} else
			throw new ResourceNotFoundException("OptionalAttributes  with the given id : " + id + "not found");
	}

	public List<OptionalAttribute> Add(OptionalAttributes OA, OptionalAttribute OAtt) {
		// test if the OptionalAttribute is already exist in the OptionalAttribute-List
		// and if not then add it
		List<OptionalAttribute> OAlist = OA.getOptionalAttributes();
		OAtt.setName(capitalize(OAtt.getName()));
		OAlist.add(OAtt);
		List<OptionalAttribute> OptionalAttributeList = new ArrayList<>();
		List<String> names = new ArrayList<>();
		for (OptionalAttribute oa : OAlist) {
			String oaname = oa.getName();
			if (!(names.contains(oaname))) {
				names.add(oa.getName());
				if (!OptionalAttributeList.contains(oa))
					OptionalAttributeList.add(oa);

			} else
				throw new ResourceNotFoundException(
						"OptionalAttribute  with the given name : " + OAtt.getName() + " already exsist");
		}
		return OptionalAttributeList;

	}

	// @PreAuthorize("hasAuthority('USER_WRITE')")
	public List<OptionalAttribute> deleteOptionalAttribute(String id, OptionalAttribute OAtt) {
		// remove an OptionalAttribute to the OptionalAttributes-List of the
		// OptionalAttributes-Object with the given ID
		Optional<OptionalAttributes> OAsDb = this.OptionalAttributesRepo.findById(id);
		List<OptionalAttribute> OptionalAttributeList = new ArrayList<>();
		if (OAsDb.isPresent()) {
			OptionalAttributes oasUpdate = OAsDb.get();
			List<OptionalAttribute> OAlist = oasUpdate.getOptionalAttributes();
			OptionalAttributeList = RemoveOptionalAttribute(OAtt, OAlist);
			oasUpdate.setOptionalAttributes(OptionalAttributeList);
			OptionalAttributesRepo.save(oasUpdate);
			return OptionalAttributeList;
		} else
			throw new ResourceNotFoundException("OptionalAttributes with the given id : " + id + "not found");
	}

	public List<OptionalAttribute> RemoveOptionalAttribute(OptionalAttribute OAtt, List<OptionalAttribute> OAlist) {
		// remove the OptionalAttribute if it's exist in the OptionalAttribute-List
		List<OptionalAttribute> OptionalAttributeList = new ArrayList<>();
		List<String> names = new ArrayList<>();
		String RemEml = OAtt.getName();
		for (OptionalAttribute oa : OAlist) {
			String oaname = oa.getName();
			if (!(names.contains(oaname))) {
				names.add(oa.getName());
			}
			if (names.contains(RemEml)) {
				if (!(RemEml.equals(oaname))) {
					if (!OptionalAttributeList.contains(oa))
						OptionalAttributeList.add(oa);
				}
			} else
				throw new ResourceNotFoundException("OptionalAttributes with the name : " + RemEml + " not exsist");

		}
		return OptionalAttributeList;

	}

	// @PreAuthorize("hasAuthority('USER_WRITE')")
	public List<OptionalAttribute> updateOptionalAttribute(String id, List<OptionalAttribute> OptionalAttributeList) {
		// update the OptionalAttribute-List of the
		// OptionalAttributes-Object with the given ID
		Optional<OptionalAttributes> OptionalAttributes = OptionalAttributesRepo.findById(id);
		if (OptionalAttributes.isPresent()) {
			OptionalAttributes OAsUpdate = OptionalAttributes.get();
			OAsUpdate.setOptionalAttributes(OptionalAttributeList);
			for (OptionalAttribute oa : OptionalAttributeList)
				oa.setName(capitalize(oa.getName()));
			OptionalAttributesRepo.save(OAsUpdate);
			return OAsUpdate.getOptionalAttributes();
		} else
			throw new ResourceNotFoundException("OptionalAttributes with the given id :" + id + " not found");

	}

	// @PreAuthorize("hasAuthority('')")
	public List<OptionalAttributes> getOptionalAttributeswithIDS(String[] ids) {
		// get a list of OptionalAttributes if their ID exists in the given ids-list
		List<OptionalAttributes> OpAttsList = new ArrayList<>();
		for (String id : ids) {
			OpAttsList.add(this.getById(id));
		}
		return OpAttsList;

	}

	// @PreAuthorize("hasAuthority('')")
	public List<OptionalAttribute> getOptionalAttributes(String id) {
		// get OptionalAttributes by the given ID
		List<OptionalAttribute> OA = new ArrayList<>();
		OptionalAttributes oas = getById(id);
		for (OptionalAttribute rest : oas.getOptionalAttributes()) {
			if (!OA.contains(rest))
				OA.add(rest);
		}
		return OA;

	}

	/**
	 * @author Joshua Müller
	 */
	public OptionalAttributes getByClassname(String classname) {
		return OptionalAttributesRepo.findByClass(classname);
	}

	/**
	 * @author Joshua Müller
	 */
	@Override
	public void testMandatoryFields(String classname, List<OptionalAttribute> optionalAttributes) {
		getByClassname(classname).testMandatoryFields(optionalAttributes);
	}

	// capitalize first letter of a string
	public static String capitalize(String str) {
		if (str == null)
			return str;
		return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();

	}

}

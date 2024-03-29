package com.dvproject.vertTerm.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dvproject.vertTerm.Model.Procedure;
import com.dvproject.vertTerm.Model.ResourceType;
import com.dvproject.vertTerm.Model.Status;
import com.dvproject.vertTerm.repository.ProcedureRepository;
import com.dvproject.vertTerm.repository.ResourceTypeRepository;


/**  author Amar Alkhankan  **/
@Service
@Transactional
public class ResourceTypeServiceImp implements ResourceTypeService {

	@Autowired
	private ResourceTypeRepository ResourceTypeRepo;
	
	@Autowired
	private ProcedureRepository procedureRepository;

	@PreAuthorize("hasAuthority('RESOURCE_TYPE_READ')")
	public List<ResourceType> getAll() {
		// get all ResourceTypes from DB
		return this.ResourceTypeRepo.findAll();
	}
	
	/**
	 * @author Joshua Müller
	 */
	@Override
	@PreAuthorize("hasAuthority('RESOURCE_TYPE_READ')")
	public List<ResourceType> getAll(Status status) {
		return ResourceTypeRepo.findByStatus(status);
	}

	@PreAuthorize("hasAuthority('RESOURCE_TYPE_READ')")
	public ResourceType getById(String id) {
		// get a ResourceType by the ResourceType-ID
		Optional<ResourceType> ResTypDb = this.ResourceTypeRepo.findById(id);
		if (ResTypDb.isPresent()) {
			return ResTypDb.get();
		} else {
			throw new ResourceNotFoundException("ResourceType with the given id :" + id + " nnnot found");
		}

	}

	@PreAuthorize("hasAuthority('RESOURCE_TYPE_WRITE')")
	public ResourceType create(ResourceType restype) {
		// create new ResourceType if not exist
		restype.setName(capitalize(restype.getName()));
		if (this.ResourceTypeRepo.findByName(restype.getName()) == null) {
			restype.setStatus(Status.ACTIVE);
			return ResourceTypeRepo.save(restype);
		}
		else	throw new ResourceNotFoundException(
					"ResourceType with the given name :" + restype.getName() + " already exists");
		

	}

	@PreAuthorize("hasAuthority('RESOURCE_TYPE_WRITE')")
	public ResourceType update(ResourceType restype) {
		// update a ResourceType if it's exist
		if (restype.getId() != null && ResourceTypeRepo.findById(restype.getId()).isPresent()) {
			restype.setName(capitalize(restype.getName()));
			return ResourceTypeRepo.save(restype);
		} else {
			throw new ResourceNotFoundException("ResourceType with the given id : " + restype.getId() + " not found ");
		}
	}

	@PreAuthorize("hasAuthority('RESOURCE_TYPE_WRITE')")
	public boolean delete(String id) {
		// delete a ResourceType from DB
		Optional<ResourceType> ResTypDb = this.ResourceTypeRepo.findById(id);
		if (ResTypDb.isPresent()) {
			ResourceType resourcetype = getById(id);
			resourcetype.setStatus(Status.DELETED);
			ResourceTypeRepo.save(resourcetype);
			removeResourceTypeFromProcedures(id);
		} else {
			throw new ResourceNotFoundException("ResourceType with the given id : " + id + " not found ");
		}
		
		return getById(id).getStatus() == Status.DELETED;
	}

	@PreAuthorize("hasAuthority('RESOURCE_TYPE_READ')")
	public List<ResourceType> getResourceTypes(String[] ids) {
		// get all ResourceTypes if their ID exists in the given ids-list
		List<ResourceType> ResTypes = new ArrayList<ResourceType>();
		for (String id : ids) {
			ResTypes.add(this.getById(id));
		}

		return ResTypes;
	}

	//capitalize first letter of a string 
	public static String capitalize(String str) {
		if (str == null)
			return str;
		return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();

	}

	/**
	 * @Author Joshua Müller
	 */
	private void removeResourceTypeFromProcedures(String resourceTypeId) {
		List<Procedure> procedureToUpdate = procedureRepository.findByNeededResourceTypesId(resourceTypeId);

		procedureToUpdate.forEach(procedure -> {
			List<ResourceType> resourceTypesOfProcedure = procedure.getNeededResourceTypes();
			resourceTypesOfProcedure.removeIf(resT -> resT.getId().equals(resourceTypeId));
			procedureRepository.save(procedure);
		});
	}

}
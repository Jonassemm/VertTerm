package com.dvproject.vertTerm.Service;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.dvproject.vertTerm.Model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.dvproject.vertTerm.repository.RessourceRepository;
import com.dvproject.vertTerm.repository.RestrictionRepository;


@Service
public class ResourceServiceImp implements ResourceService {


	@Autowired
	private RessourceRepository ResRepo;
	@Autowired
	private RestrictionRepository RestsRepo;


	//@PreAuthorize("hasAuthority('RESOURCE_DATA_READ')")
	public List<Resource> getAll() {
		return this.ResRepo.findAll();
	}

	public List<Resource> getAll(ResourceType type){
		List<Resource> result= new ArrayList<>();
		for(Resource resource : ResRepo.findAll()){
			if(resource.getResourceType().equals(type)) {
				result.add(resource);
			}
		}
		return result;
	}


	//@PreAuthorize("hasAuthority('RESOURCE_DATA_READ')")
	public Resource getById(String id) {
		Optional<Resource> ResDb = this.ResRepo.findById(id);
		if (ResDb.isPresent())
			return ResDb.get();
		else
			throw new ResourceNotFoundException("Resource with the given id :" + id + " already exists");
	}

	//@PreAuthorize("hasAuthority('RESOURCE_DATA_WRITE')")
	public Resource create(Resource res) {
		if (this.ResRepo.findByName(res.getName()) == null)
			return ResRepo.save(res);
		else
			throw new ResourceNotFoundException("Resource with the given id :" + res.getId() + " already exists");

	}


	//@PreAuthorize("hasAuthority('RESOURCE_DATA_WRITE')")
	public Resource update(Resource res) {
		if (res.getId() != null && ResRepo.findById(res.getId()).isPresent()) {
			return ResRepo.save(res);
		} else {
			throw new ResourceNotFoundException("Resource with the given id :" + res.getId() + "not found");
		}
	}

	//@PreAuthorize("hasAuthority('RESOURCE_STATUS_WRITE')")
	public boolean delete(String id) {
		Resource Res = getById(id);
		return Res.getStatus() == Status.DELETED;
	}


	//@PreAuthorize("hasAuthority('RESOURCE_DATA_READ')")
	public List<Resource> getResources(String[] ids) {
		List<Resource> Resources = new ArrayList<>();
		for (String id : ids) {
			Resources.add(this.getById(id));
		}
		return Resources;
	}

	//@PreAuthorize("hasAuthority('RESOURCE_DATA_READ')")
	public List<Restriction> getResourceDependencies(String id) {
		List<Restriction> dep = new ArrayList<>();
		Resource res = getById(id);
		for (Restriction rest : res.getRestrictions()) {
			if (!dep.contains(rest))
				dep.add(rest);
		}
		return dep;

	}

	//@PreAuthorize("hasAuthority('RESOURCE_AVAILABILITIES_WRITE')")
	public Resource updateResourceAvailability(Resource res) {
		Optional<Resource> ResDb = this.ResRepo.findById(res.getId());
		if (ResDb.isPresent()) {
			Resource ResUpdate = ResDb.get();
			ResUpdate.setAvailabilities(res.getAvailability());
			ResRepo.save(ResUpdate);
			return ResUpdate;
		} else {
			throw new ResourceNotFoundException("Resource with the given id :" + res.getId() + " already exists");

		}
	}

	//@PreAuthorize("hasAuthority('RESOURCE_DATA_WRITE')")
	public List<Restriction> updateResourceDependencies(String id, String[] Rids) {
		List<Restriction> dep = new ArrayList<>();
		List<Restriction> AllRestrictions = RestsRepo.findAll();
		List<String> RestIds = List.of(Rids);
		for (Restriction rest : AllRestrictions) {
			String rid = rest.getId();
			if (RestIds.contains(rid)) {
				if (!dep.contains(rest))
					dep.add(rest);
			}

		}
		Optional<Resource> ResDb = this.ResRepo.findById(id);
		if (ResDb.isPresent()) {
			Resource ResUpdate = ResDb.get();
			ResUpdate.setRestrictions(dep);
			ResRepo.save(ResUpdate);
			return dep;
		} else {
			throw new ResourceNotFoundException("Resource with the given id :" + id + " already exists");

		}
	}


}



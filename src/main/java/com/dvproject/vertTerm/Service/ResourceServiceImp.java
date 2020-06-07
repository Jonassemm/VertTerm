package com.dvproject.vertTerm.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import com.dvproject.vertTerm.Model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.dvproject.vertTerm.Model.Availability;
import com.dvproject.vertTerm.Model.Procedure;
import com.dvproject.vertTerm.Model.Resource;
import com.dvproject.vertTerm.Model.ResourceType;
import com.dvproject.vertTerm.Model.Restriction;
import com.dvproject.vertTerm.Model.Right;
import com.dvproject.vertTerm.Model.Status;
import com.dvproject.vertTerm.repository.RessourceRepository;
import com.dvproject.vertTerm.repository.RestrictionRepository;

@Service
public class ResourceServiceImp implements ResourceService {

	@Autowired
	private RessourceRepository ResRepo;
	@Autowired
	private RestrictionRepository RestsRepo;
	@Autowired
	private AvailabilityServiceImpl availabilityService;

	// @PreAuthorize("hasAuthority('RESOURCE_DATA_READ')")
	public List<Resource> getAll() {
		return this.ResRepo.findAll();
	}

	// @PreAuthorize("hasAuthority('RESOURCE_DATA_READ')")
	public Resource getById(String id) {
		Optional<Resource> ResDb = this.ResRepo.findById(id);
		if (ResDb.isPresent())
			return ResDb.get();
		else
			throw new ResourceNotFoundException("Resource with the given id :" + id + " already exists");
	}

	// @PreAuthorize("hasAuthority('RESOURCE_DATA_WRITE')")
	public Resource create(Resource res) {
		if (this.ResRepo.findByName(res.getName()) == null)
			return ResRepo.save(res);
		else
			throw new ResourceNotFoundException("Resource with the given id :" + res.getId() + " already exists");

	}


	// @PreAuthorize("hasAuthority('RESOURCE_STATUS_WRITE')")
	public boolean delete(String id) {
		Resource Res = getById(id);
		Res.setStatus(Status.DELETED);
		ResRepo.save(Res);
		return Res.getStatus() == Status.DELETED;
	}

	// @PreAuthorize("hasAuthority('RESOURCE_DATA_WRITE')")
	public Resource update(Resource res) {
		if (res.getId() != null && ResRepo.findById(res.getId()).isPresent()) {
			return ResRepo.save(res);
		} else {
			throw new ResourceNotFoundException("Resource with the given id :" + res.getId() + "not found");
		}
	}

	// @PreAuthorize("hasAuthority('RESOURCE_DATA_READ')")
	public List<Resource> getResources(String[] ids) {
		List<Resource> Resources = new ArrayList<>();
		for (String id : ids) {
			Resources.add(this.getById(id));
		}
		return Resources;
	}

	@Override
	public List<Resource> getAll(ResourceType type) {
		return this.getResources(type.getId());
	}

	// @PreAuthorize("hasAuthority('RESOURCE_DATA_READ')")
	public List<Restriction> getResourceRestrictions(String id) {
		List<Restriction> dep = new ArrayList<>();
		Resource res = getById(id);
		for (Restriction rest : res.getRestrictions()) {
			if (!dep.contains(rest))
				dep.add(rest);
		}
		return dep;

	}

	// @PreAuthorize("hasAuthority('RESOURCE_DATA_READ')")
	public List<Restriction> getResourceDependencies(String id) {
		List<Restriction> dep = new ArrayList<>();
		Resource res = getById(id);
		for (Restriction rest : res.getRestrictions()) {
			if (!dep.contains(rest))
				dep.add(rest);
		}
		return dep;

	}

	// @PreAuthorize("hasAuthority('RESOURCE_AVAILABILITIES_WRITE')")
	public List<Availability> updateResourceAvailabilities(String id, List<Availability> availabilities) {
		Optional<Resource> ResDb = this.ResRepo.findById(id);
		if (ResDb.isPresent()) {
			Resource ResUpdate = ResDb.get();
			Resource res = getById(id);
			ResUpdate.getAvailabilities().clear();
			ResUpdate.getAvailabilities().addAll(res.getAvailabilities());
			ResRepo.save(ResUpdate);
			return res.getAvailabilities();
		} else {
			throw new ResourceNotFoundException("Resource with the given id :" + id + "not found");

		}
	}

	// @PreAuthorize("hasAuthority('RESOURCE_DATA_WRITE')")
	public List<Restriction> updateResourceRestrictions(String id, String[] Rids) {
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
			throw new ResourceNotFoundException("Resource with the given id :" + id + "not found");

		}
	}

	@Override
	public List<Resource> getResources(Status status) {
		return ResRepo.findByStatus(status);
	}

	@Override
	public List<ResourceType> getResourceTypes(String id) {
		Resource Resource = getById(id);
		return Resource.getResourceTypes();
	}

	@Override
	public List<Resource> getResources(String ResTid) {
		List<Resource> Resources = new ArrayList<>();
		List<Resource> AllResources = ResRepo.findAll();
		for (Resource r : AllResources) {
			List<ResourceType> ReTys = r.getResourceTypes();
			for (ResourceType RT : ReTys) {
				String RTID = RT.getId();
				if (RTID.equals(ResTid)) {
					if (!Resources.contains(r))
						Resources.add(r);
				}
			}
		}
		return Resources;
	}

	@Override
	public boolean isResourceAvailableBetween(String id, Date startdate, Date enddate) {
		Resource Res = getById(id);
		return availabilityService.isAvailable(Res.getAvailabilities(), startdate, enddate);
	}

	// @PreAuthorize("hasAuthority('RESOURCE_DATA_WRITE')")
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

	/**
	 * 
	 * @param newInstance resource to update
	 * 
	 * @throws RuntimeException if a cyclical dependency has been found
	 */
	private void hasCorrectDependencies(Resource newInstance) {
		List<Resource> oldInstanceChildResources;
		List<Resource> resourcesToTest = new ArrayList<>();

		if (newInstance.getChildResources() == null) {
			return;
		}

		try {
			oldInstanceChildResources = this.getById(newInstance.getId()).getChildResources();
		} catch (ResourceNotFoundException ex) {
			// if there is no resource with the corresponding id in the db, there can be no cycical dependency
			return;
		}

		for (Resource resource : newInstance.getChildResources()) {
			if (!oldInstanceChildResources.contains(resource)) {
				resourcesToTest.add(resource);
			}
		}

		while (!resourcesToTest.isEmpty()) {
			Resource resource = resourcesToTest.get(0);
			List<Resource> childResources = resource.getChildResources();

			if (resource.getId().equals(newInstance.getId())) {
				throw new RuntimeException("Cyclical dependencies found");
			}
			if (childResources != null) {
				resourcesToTest.addAll(0, childResources);
			}
		}
	}
}

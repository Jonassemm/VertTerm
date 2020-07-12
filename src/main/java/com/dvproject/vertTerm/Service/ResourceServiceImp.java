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

import com.dvproject.vertTerm.repository.RessourceRepository;
import com.dvproject.vertTerm.repository.RestrictionRepository;

//author Amar Alkhankan 
//testCorrectDependencies Methode ?
@Service
public class ResourceServiceImp extends WarningServiceImpl implements ResourceService, AvailabilityService {

	@Autowired
	private RessourceRepository ResRepo;
	@Autowired
	private RestrictionRepository RestsRepo;
	@Autowired
	private AvailabilityServiceImpl availabilityService;
	@Autowired
	private ResourceTypeService  RestypeService;
	@Autowired
	private AppointmentService appointmentService;

	// @PreAuthorize("hasAuthority('RESOURCE_READ')")
	public List<Resource> getAll() {
		return this.ResRepo.findAll();
	}

	// @PreAuthorize("hasAuthority('RESOURCE_READ')")
	public Resource getById(String id) {
		Optional<Resource> ResDb = this.ResRepo.findById(id);
		if (ResDb.isPresent())
			return ResDb.get();
		else
			throw new ResourceNotFoundException("Resource with the given id :" + id + " already exists");
	}

	// @PreAuthorize("hasAuthority('RESOURCE_WRITE')")
	public Resource create(Resource res) {
		res.setName(capitalize(res.getName()));
		if (this.ResRepo.findByName(res.getName()) == null) {
			availabilityService.update(res.getAvailabilities(), res);
			return ResRepo.save(res);
		} else
			throw new ResourceNotFoundException("Resource with the given name : " + res.getName() + " already exists");

	}

	// @PreAuthorize("hasAuthority('RESOURCE_WRITE')")
	public boolean delete(String id) {
		// change resource_Status to 'DELETED'
		Resource Res = getById(id);
		Res.setStatus(Status.DELETED);
		ResRepo.save(Res);

		getPlannedAppointmentsWithId(id).forEach(app -> {
			app.addWarnings(Warning.RESOURCE_WARNING);
			appointmentService.update(app);
		});

		return Res.getStatus() == Status.DELETED;
	}

	// @PreAuthorize("hasAuthority('RESOURCE_WRITE')")
	public Resource update(Resource res) {
		String resId = res.getId();
		Optional<Resource> resource = resId != null ? ResRepo.findById(resId) : null;
		Resource retVal = null;

		if (resId != null && resource.isPresent()) {
			testCorrectDependencies(res);
			availabilityService.loadAllAvailabilitiesOfEntity(res.getAvailabilities(), res, this);
			res.setName(capitalize(res.getName()));
			retVal = ResRepo.save(res);
			testWarningsFor(resId);
			return retVal;
		} else {
			throw new ResourceNotFoundException("Resource with the given id :" + res.getId() + "not found");
		}
	}

	// @PreAuthorize("hasAuthority('RESOURCE_READ')")
	public List<Resource> getResources(String[] ids) {
		List<Resource> Resources = new ArrayList<>();
		for (String id : ids) {
			Resources.add(this.getById(id));
		}
		return Resources;
	}

	// @PreAuthorize("hasAuthority('RESOURCE_READ')")
	public List<Resource> getAll(ResourceType type) {
		return this.getResources(type.getId());
	}

	// @PreAuthorize("hasAuthority('RESOURCE_READ')")
	public List<Restriction> getResourceRestrictions(String id) {
		List<Restriction> dep = new ArrayList<>();
		Resource res = getById(id);
		for (Restriction rest : res.getRestrictions()) {
			if (!dep.contains(rest))
				dep.add(rest);
		}
		return dep;

	}

	// @PreAuthorize("hasAuthority('RESOURCE_READ')")
	public List<Availability> getAllAvailabilities(String id) {
		// get resource availabilities
		Resource resource = this.getById(id);
		if (resource == null) { throw new IllegalArgumentException("No resource with the given id"); }

		return resource.getAvailabilities();
	}

	// @PreAuthorize("hasAuthority('RESOURCE_WRITE')")
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

	// @PreAuthorize("hasAuthority('RESOURCE_WRITE')")
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

	// @PreAuthorize("hasAuthority('RESOURCE_READ')")
	public List<Resource> getResources(Status status) {
		return ResRepo.findByStatus(status);
	}

	// @PreAuthorize("hasAuthority('RESOURCE_READ')")
	public List<ResourceType> getResourceTypes(String id) {
		Resource Resource = getById(id);
		return Resource.getResourceTypes();
	}

	
	// @PreAuthorize("hasAuthority('RESOURCE_READ')")
	public List<Resource> getResources(String ResTid) {
		// get all resources from resource-type using id
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


	// @PreAuthorize("hasAuthority('RESOURCE_READ')")
		public List<Resource> getResourcesbyResourceTypeandStatus(String RTid,Status status) {
			// get all resources of a specific resource type and status
			List<Resource> Resources = new ArrayList<>();
			ResourceType type= RestypeService.getById(RTid);
			List<Resource> AllResources = this.getAll(type);
			for (Resource res : AllResources) {
				if (res.getStatus().equals(status)) {
					if (!Resources.contains(res))
						Resources.add(res);
				}
			}
			return Resources;
		}

	// @PreAuthorize("hasAuthority('RESOURCE_READ')")
	public boolean isResourceAvailableBetween(String id, Date startdate, Date enddate) {
		Resource Res = getById(id);
		return availabilityService.isAvailable(Res.getAvailabilities(), startdate, enddate);
	}

	/**
	 * 
	 * @param newInstance resource to update
	 * 
	 * @throws RuntimeException if a cyclical dependency has been found
	 */
	private void testCorrectDependencies(Resource newInstance) {
		List<Resource> oldInstanceChildResources;
		List<Resource> resourcesToTest = new ArrayList<>();

		if (newInstance.getChildResources() == null) { return; }

		try {
			oldInstanceChildResources = this.getById(newInstance.getId()).getChildResources();
		} catch (ResourceNotFoundException ex) {
			// if there is no resource with the corresponding id in the db, there can be no
			// cycical dependency
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

			resourcesToTest.remove(0);
		}
	}

	// capitalize first letter of a string
	public static String capitalize(String str) {
		if (str == null)
			return str;

		return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();

	}

	@Override
	public List<Appointment> getPlannedAppointmentsWithId(String id) {
		return appointmentService.getAppointmentsByResourceIdAndAppointmentStatus(id, AppointmentStatus.PLANNED);
	}

}

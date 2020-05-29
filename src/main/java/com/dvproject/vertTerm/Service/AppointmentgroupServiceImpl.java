package com.dvproject.vertTerm.Service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import com.dvproject.vertTerm.Model.Appointmentgroup;
import com.dvproject.vertTerm.Model.Status;
import com.dvproject.vertTerm.repository.AppointmentRepository;
import com.dvproject.vertTerm.repository.AppointmentgroupRepository;

@Service
public class AppointmentgroupServiceImpl implements AppointmentgroupService {
	@Autowired
	private AppointmentgroupRepository repo;

	@Autowired
	private AppointmentRepository appoint;
	
	@Autowired
	private StatusService statusService;

	@Override
	public List<Appointmentgroup> getAll() {
		return repo.findAll();
	}

	@Override
	public Appointmentgroup getById(String id) {
		return this.getAppointmentInternal(id);
	}

	@Override
	public Appointmentgroup create(Appointmentgroup newInstance) {
		if (!repo.existsById(newInstance.getId())) {
			return repo.save(newInstance);
		}
		return null;
	}

	@Override
	public Appointmentgroup update(Appointmentgroup updatedInstance) {
		if (repo.existsById(updatedInstance.getId())) {
			if (! statusService.isUpdateable(updatedInstance.getStatus())) {
				throw new IllegalArgumentException("The given procedure is not updateable");
			}
			return repo.save(updatedInstance);
		}
		return null;
	}

	@Override
	public boolean delete(String id) {
		this.deleteAppointmentgroup(id);

		Appointmentgroup app = this.getAppointmentInternal(id);

		return app.getStatus() == Status.DELETED;
	}

	private Appointmentgroup deleteAppointmentgroup(String id) {
		Appointmentgroup app = this.getAppointmentInternal(id);
		app.setStatus(Status.DELETED);
		return repo.save(app);
	}

	@Override
	public Appointmentgroup getAppointmentgroupWithAppointmentID(String id) {
		if (id == null) {
			throw new NullPointerException("The id of the given appointment is null");
		}
		if (appoint.findById(id).isEmpty()) {
			throw new ResourceNotFoundException("The id of the given appointment is invalid");
		}

		return repo.findByAppointmentsId(id);
	}

	private Appointmentgroup getAppointmentInternal(String id) {
		if (id == null) {
			throw new ResourceNotFoundException("The id of the given appointmentgroup is null");
		}

		Optional<Appointmentgroup> appGr = repo.findById(id);

		if (appGr.isPresent()) {
			return appGr.get();
		} else {
			throw new ResourceNotFoundException("No appointmentgroup with the given id (" + id + ") can be found.");
		}
	}

}

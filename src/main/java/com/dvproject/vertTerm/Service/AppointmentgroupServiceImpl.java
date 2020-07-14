package com.dvproject.vertTerm.Service;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

import javax.servlet.http.HttpServletResponse;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.dvproject.vertTerm.Model.*;
import com.dvproject.vertTerm.exception.*;
import com.dvproject.vertTerm.repository.*;
import com.dvproject.vertTerm.util.*;

/**
 * @author Joshua Müller
 */
@Service
public class AppointmentgroupServiceImpl implements AppointmentgroupService {
	@Autowired
	private AppointmentgroupRepository appointmentgroupRepository;

	@Autowired
	private AppointmentServiceImpl appointmentService;

	@Autowired
	private AppointmentRepository appointmentRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RestrictionService restrictionService;

	@Autowired
	private HttpServletResponse httpResponse;

	@Override
	@PreAuthorize("hasAuthority('APPOINTMENT_READ')")
	public List<Appointmentgroup> getAll() {
		return appointmentgroupRepository.findAll();
	}

	@Override
	@PreAuthorize("hasAuthority('APPOINTMENT_READ')")
	public List<Appointmentgroup> getAppointmentgroupsWithStatus(Status status) {
		return appointmentgroupRepository.findByStatus(status);
	}

	@Override
	@PreAuthorize("hasAuthority('APPOINTMENT_READ')")
	public Appointmentgroup getAppointmentgroupContainingAppointmentID(String id) {
		if (id == null)
			throw new NullPointerException("The id of the given appointment is null");

		if (appointmentService.getById(id) == null)
			throw new ResourceNotFoundException("The id of the given appointment is invalid");

		return appointmentgroupRepository.findByAppointmentsId(id);
	}

	@Override
	@PreAuthorize("hasAuthority('APPOINTMENT_READ')")
	public Appointmentgroup getById(String id) {
		return getAppointmentgroupInternal(id);
	}

	@Override
	public void loadAppointmentgroupWithOverride(Appointmentgroup appointmentgroupToLoad) {
		List<Appointment> appointmentsToTest = appointmentgroupToLoad.getAppointments();
		if (appointmentsToTest == null || appointmentsToTest.size() == 0)
			throw new IllegalArgumentException("No Appointment to load");

		for (Appointment appointment : appointmentsToTest) {
			appointmentgroupToLoad.setStatus(Status.ACTIVE);

			try {
				appointmentService.loadAppointment(appointment);
			} catch (ProcedureException ex) {
				appointment.addWarning(Warning.PROCEDURE_WARNING);
			}
		}
	}

	@Override
	public void loadAppointmentgroup(Appointmentgroup appointmentgroupToLoad) {
		List<Appointment> appointmentsToTest = appointmentgroupToLoad.getAppointments();
		if (appointmentsToTest == null || appointmentsToTest.size() == 0)
			throw new IllegalArgumentException("No Appointment to load");

		for (Appointment appointment : appointmentsToTest) {
			appointmentgroupToLoad.setStatus(Status.ACTIVE);
			appointmentService.loadAppointment(appointment);
		}
	}

	@Override
	@PreAuthorize("hasAuthority('APPOINTMENT_WRITE')")
	public void testWarningsForAppointments(List<Appointment> appointmentsToTest) {
		appointmentsToTest = appointmentService.cleanseAppointmentsOfBlocker(appointmentsToTest);
		List<String> appointmentIdsTested = new ArrayList<>();

		for (Appointment appointmentToTest : appointmentsToTest) {
			String appointmentIdToTest = appointmentToTest.getId();

			// has already been tested or does not need to be tested
			if (appointmentIdsTested.contains(appointmentIdToTest) || appointmentToTest.getStatus().isDeleted())
				continue;

			Appointmentgroup appointmentgroupToTest = getAppointmentgroupContainingAppointmentID(appointmentIdToTest);

			appointmentgroupToTest.resetAllWarnings();

			appointmentgroupToTest.testWarnings(appointmentService, restrictionService, userRepository);

			saveAppointmentgroup(appointmentgroupToTest);

			List<Appointment> appointmentsOfAppointmentgroupToTest = appointmentgroupToTest.getAppointments();
			// set ids of the tested appointments
			appointmentsOfAppointmentgroupToTest.forEach(app -> appointmentIdsTested.add(app.getId()));
		}
	}

	@Override
	public void setPullableAppointment(Appointment appointment) {
		Date starttime = getDateOfNowRoundedUp();
		appointment.generateNewDatesFor(starttime);

		if (isPullable(appointment)) {
			httpResponse.setHeader("appointmentid", appointment.getId());
			httpResponse.setHeader("starttime", getStringRepresentation(appointment.getPlannedStarttime()));
		}
	}

	@Override
	public void setPullableAppointments(Appointment appointment) {
		Date starttime = getDateOfNowRoundedUp();
		List<Appointment> appointmentsToPull = getPullableAppointments(starttime, appointment);
		boolean listIsSet = appointmentsToPull != null && appointmentsToPull.size() > 0;

		if (listIsSet) {
			httpResponse.addHeader("starttime", getStringRepresentation(starttime));
			appointmentsToPull.forEach(app -> httpResponse.addHeader("appointmentid", app.getId()));
		}
	}

	@Override
	@PreAuthorize("hasAuthority('APPOINTMENT_WRITE')")
	public void saveAppointmentgroup(Appointmentgroup appointmentgroup) {
		List<Appointment> appointments = appointmentgroup.getAppointments();
		appointments.forEach(appointment -> appointmentRepository.save(appointment));
		appointmentgroupRepository.save(appointmentgroup);
	}

	@Override
	@PreAuthorize("hasAuthority('APPOINTMENT_WRITE')")
	public boolean startAppointment(Appointment appointment) {
		if (appointment.notStartedOrEnded())
			throw new UnsupportedOperationException("You can not start an appointment that has already been started");

		appointment.setActualStarttime(getDateOfNow());

		appointment = appointmentService.update(appointment);

		return appointment.getActualStarttime() != null;
	}

	@Override
	@PreAuthorize("hasAuthority('APPOINTMENT_WRITE')")
	public boolean stopAppointment(Appointment appointment) {
		if (appointment.started())
			throw new UnsupportedOperationException("You can only stop an appointment that has already been started");

		appointment.setActualEndtime(getDateOfNow());
		appointment.setStatus(AppointmentStatus.DONE);

		appointment = appointmentService.update(appointment);

		return appointment.getActualEndtime() != null && appointment.getStatus().isDone();
	}

	@Override
	@PreAuthorize("hasAuthority('APPOINTMENT_WRITE')")
	public boolean delete(String id) {
		Appointmentgroup appointmentgroup = deleteAppointmentgroupInternal(id);

		return appointmentgroup.getStatus().isDeleted();
	}

	@Override
	@PreAuthorize("hasAuthority('APPOINTMENT_WRITE')")
	public boolean deleteAppointment(Appointment appointment, Booker booker) {
		String appointmentid = appointment.getId();
		Appointmentgroup appointmentgroup = getAppointmentgroupContainingAppointmentID(appointmentid);
		List<Appointment> appointments = appointmentgroup.getAppointments();
		appointment = appointments.stream().filter(app -> app.hasSameIdAs(appointmentid)).findAny().get();

		appointment.setStatus(AppointmentStatus.DELETED);

		booker.setAppointmentgroup(appointmentgroup);
		booker.testProcedureRelations();
		saveAppointmentgroup(appointmentgroup);

		return appointmentService.delete(appointmentid);
	}

	private boolean isPullable(Appointment appointment) {
		if (!appointment.isCustomerIsWaiting())
			return false;

		Appointmentgroup appointmentgroup = getAppointmentgroupContainingAppointmentID(appointment.getId());
		NormalBooker booker = new NormalBooker(appointmentgroup);
		List<Appointment> appointments = appointmentgroup.getAppointments();

		appointments.removeIf(app -> app.getId().equals(appointment.getId()));
		appointments.add(appointment);

		try {
			booker.bookable(appointmentService, restrictionService, userRepository);
		} catch (RuntimeException ex) {
			return false;
		}

		return true;
	}

	private Appointmentgroup deleteAppointmentgroupInternal(String id) {
		Appointmentgroup appointmentgroup = getAppointmentgroupInternal(id);
		appointmentgroup.setStatus(Status.DELETED);
		return appointmentgroupRepository.save(appointmentgroup);
	}

	private Appointmentgroup getAppointmentgroupInternal(String id) {
		if (id == null)
			throw new ResourceNotFoundException("The id of the given appointmentgroup is null");

		Optional<Appointmentgroup> appointmentgroup = appointmentgroupRepository.findById(id);

		if (appointmentgroup.isPresent()) {
			return appointmentgroup.get();
		} else
			throw new ResourceNotFoundException("No appointmentgroup with the given id (" + id + ") can be found.");
	}

	private List<Appointment> getPullableAppointments(Date startdate, Appointment appointment) {
		List<Appointment> appointmentsToTest;

		if (appointment == null) {
			appointmentsToTest = appointmentService.getAppointmentsInTimeIntervalAndStatus(startdate,
					getLatestTimeOfToday(), AppointmentStatus.PLANNED);
		} else {
			// create object-ids from the id strings because mongodb can not handle a list
			// of strings
			List<ObjectId> employeeids = appointment.idsOfEmployees();
			List<ObjectId> resourceids = appointment.idsOfResources();

			appointmentsToTest = appointmentService.getAppointmentsWithCustomerEmployeeResourceAfterDate(employeeids,
					resourceids, startdate, AppointmentStatus.PLANNED);
		}

		appointmentsToTest = appointmentService.cleanseAppointmentsOfBlocker(appointmentsToTest);

		appointmentsToTest.removeIf(app -> !isPullable(app.getAppointmentWithNewDatesFor(startdate)));

		return appointmentsToTest;
	}

//	private Appointmentgroup getUpdatableAppointmentgroupContainingAppointmentID(String appointmentid) {
//		Appointmentgroup appointmentgroup = getAppointmentgroupContainingAppointmentID(appointmentid);
//		List<Appointment> appointments = appointmentgroup.getAppointments();
//		boolean isUpdateble = appointmentgroup.getStatus().isActive();
//
//		isUpdateble &= appointments.stream().noneMatch(app -> app.notStartedOrEnded() && app.getStatus().isDeleted());
//
//		return isUpdateble ? appointmentgroup : null;
//	}

	private Date getDateOfNow() {
		LocalDateTime nowInOtherTimeZone = LocalDateTime.ofInstant(Instant.now(), ZoneId.of("CET"));
		return Date.from(nowInOtherTimeZone.atZone(ZoneId.systemDefault()).toInstant());
	}

	private Date getDateOfNowRoundedUp() {
		int addedMinutes = 2;
		Calendar cal = getCalendar();
		cal.set(Calendar.MINUTE, cal.get(Calendar.MINUTE) + addedMinutes);
		cal.set(Calendar.SECOND, 0);

		return cal.getTime();
	}

	private Date getLatestTimeOfToday() {
		Calendar calendar = getCalendar();
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);

		return calendar.getTime();
	}

	private Calendar getCalendar() {
		Calendar cal = Calendar.getInstance(TimeZone.getDefault());
		cal.setTime(getDateOfNow());
		return cal;
	}

	private String getStringRepresentation(Date date) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
		LocalDateTime ldt = date.toInstant().atZone(ZoneId.of("UTC")).toLocalDateTime();

		return formatter.format(ldt);
	}

}

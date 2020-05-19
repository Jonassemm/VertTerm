package com.dvproject.vertTerm.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import com.dvproject.vertTerm.Model.Availability;
import com.dvproject.vertTerm.Model.AvailabilityRhythm;
import com.dvproject.vertTerm.Model.Position;
import com.dvproject.vertTerm.Model.Procedure;
import com.dvproject.vertTerm.Model.ProcedureRelation;
import com.dvproject.vertTerm.Model.ResourceType;
import com.dvproject.vertTerm.Model.Restriction;
import com.dvproject.vertTerm.Model.Status;
import com.dvproject.vertTerm.repository.ProcedureRepository;

@Service
public class ProcedureServiceImp implements ProcedureService {
	@Autowired
	private ProcedureRepository procedureRepository;

	@Override
	public List<Procedure> getAllProcedures() {
		return procedureRepository.findAll();
	}

	@Override
	public List<Procedure> getProcedures(String[] ids) {
		return procedureRepository.findByIds(ids);
	}

	@Override
	public List<ProcedureRelation> getPrecedingProcedures(String id) {
		return getProcedureInternal(id).getPrecedingRelations();
	}

	@Override
	public List<ProcedureRelation> getSubsequentProcedures(String id) {
		return getProcedureInternal(id).getSubsequentRelations();
	}

	@Override
	public List<ResourceType> getNeededResourceTypes(String id) {
		return this.getProcedureInternal(id).getNeededResourceTypes();
	}

	@Override
	public List<Position> getNeededPositions(String id) {
		return this.getProcedureInternal(id).getNeededEmployeePositions();
	}

	@Override
	public List<Availability> getAllAvailabilities(String id) {
		return this.getProcedureInternal(id).getAvailabilities();
	}

	@Override
	public List<Restriction> getProcedureRestrictions(String id) {
		return this.getProcedureInternal(id).getRestrictions();
	}

	@Override
	public boolean isAvailableBetween (String id, Date startdate, Date enddate) {
		List<Availability> availabilities = getProcedureInternal(id).getAvailabilities();
		boolean isAvailable = false;
		
		for (Availability availability : availabilities) {
			if (! (availability.getStartDate().after(startdate) && (availability.getEndOfSeries() == null || availability.getEndOfSeries().before(enddate)))) {
				Date availabilityStartdate = availability.getStartDate();
				Date availabilityEnddate = availability.getEndDate();
				
				isAvailable = isBetween (getCalendar(startdate), getCalendar(enddate), 
							getCalendar(availabilityStartdate), getCalendar(availabilityEnddate), 
							availability.getRhythm());
				
				if (isAvailable)
					return true;
			}
		}
		return false;
	}

	@Override
	public Procedure insertProcedure(Procedure procedure) {
		if (procedure.getId() == null) {
			return procedureRepository.save(procedure);
		}
		if (procedureRepository.findById(procedure.getId()).isPresent()) {
			throw new ResourceNotFoundException("Procedure with the given id (" + procedure.getId() + ") exists on the database. Use the update method.");
		}
		return procedure;
	}

	@Override
	public Procedure updateProcedure(Procedure procedure) {
		if (procedureRepository.findById(procedure.getId()).isPresent()) {
			return procedureRepository.save(procedure);
		} else {
			throw new ResourceNotFoundException("No procedure with the given id (" + procedure.getId() + ") can be found.");
		}
	}

	@Override
	public Procedure updateProceduredata(Procedure procedure) {
		Procedure oldProcedure = getProcedureInternal(procedure.getId());
		
		oldProcedure.setName(procedure.getName());
		oldProcedure.setDescription(procedure.getDescription());
		oldProcedure.setPricePerHour(procedure.getPricePerHour());
		oldProcedure.setPricePerInvocation(procedure.getPricePerInvocation());
		oldProcedure.setDurationInMinutes(procedure.getDurationInMinutes());
		
		return procedureRepository.save(oldProcedure);
	}
	
	@Override
	public List<ProcedureRelation> updatePrecedingProcedures(String id, List<ProcedureRelation> precedingProcedures) {
		Procedure procedure = getProcedureInternal(id);
		
		procedure.setPrecedingRelations(precedingProcedures);
		procedureRepository.save(procedure);
		
		return getProcedureInternal(id).getPrecedingRelations();
	}

	@Override
	public List<ProcedureRelation> updateSubsequentProcedures(String id, List<ProcedureRelation> subsequentProcedures) {
		Procedure procedure = getProcedureInternal(id);
		
		procedure.setSubsequentRelations(subsequentProcedures);
		procedureRepository.save(procedure);
		
		return getProcedureInternal(id).getSubsequentRelations();
	}

	@Override
	public List<ResourceType> updateNeededResourceTypes(String id, List<ResourceType> resourceTypes) {
		Procedure procedure = getProcedureInternal(id);
		
		procedure.setNeededResourceTypes(resourceTypes);
		procedureRepository.save(procedure);
		
		return getProcedureInternal(id).getNeededResourceTypes();
	}

	@Override
	public List<Position> updateNeededPositions(String id, List<Position> positions) {
		Procedure procedure = getProcedureInternal(id);
		
		procedure.setNeededEmployeePositions(positions);
		procedureRepository.save(procedure);
		
		return getProcedureInternal(id).getNeededEmployeePositions();
	}

	@Override
	public List<Availability> updateAllAvailabilities(String id, List<Availability> availabilities) {
		Procedure procedure = getProcedureInternal(id);
		
		procedure.setAvailabilities(availabilities);
		procedureRepository.save(procedure);
		
		return getProcedureInternal(id).getAvailabilities();
	}

	@Override
	public List<Restriction> updateProcedureRestrictions(String id, List<Restriction> restrictions) {
		Procedure procedure = getProcedureInternal(id);
		
		procedure.setRestrictions(restrictions);
		procedureRepository.save(procedure);
		
		return getProcedureInternal(id).getRestrictions();
	}

	@Override
	public Procedure deleteProcedure(String id) {
		Procedure procedure = getProcedureInternal(id);

		procedure.setStatus(Status.DELETED);

		return procedureRepository.save(procedure);
	}
	
	public boolean isDeleted (String id) {
		Procedure procedure = getProcedureInternal(id);
		
		return procedure.getStatus() == Status.DELETED;
	}

	private Procedure getProcedureInternal(String id) {
		if (id == null) {
			throw new ResourceNotFoundException("The id of the given procedure is null");
		}
		
		Optional<Procedure> procedureDB = procedureRepository.findById(id);
		
		if (procedureDB.isPresent()) {
			return procedureDB.get();
		} else {
			throw new ResourceNotFoundException("No procedure with the given id (" + id + ") can be found.");
		}
	}
	
	private boolean isBetween (Calendar startdate, Calendar enddate, Calendar availStartdate, Calendar availEnddate, 
			AvailabilityRhythm rhythm) {
		boolean isBetween = false;
		switch (rhythm) {
			case DAILY:
				isBetween = true;
				break;
			case WEEKLY:
				isBetween = availStartdate.get(Calendar.DAY_OF_WEEK) == startdate.get(Calendar.DAY_OF_WEEK)
					&& enddate.get(Calendar.DAY_OF_WEEK) == availEnddate.get(Calendar.DAY_OF_WEEK);
				break;
				
			case MONTHLY:
				isBetween = availStartdate.get(Calendar.DAY_OF_MONTH) == startdate.get(Calendar.DAY_OF_MONTH)
					&& enddate.get(Calendar.DAY_OF_MONTH) == availEnddate.get(Calendar.DAY_OF_MONTH);
				break;
				
			case YEARLY:
				isBetween = availStartdate.get(Calendar.DAY_OF_YEAR) == startdate.get(Calendar.DAY_OF_YEAR)
					&& enddate.get(Calendar.DAY_OF_YEAR) == availEnddate.get(Calendar.DAY_OF_YEAR);
				break;
				
			default:
				return false;
		}
		int availabilityDistance = availEnddate.get(Calendar.DAY_OF_YEAR) - availStartdate.get(Calendar.DAY_OF_YEAR);
		int dayDistance = enddate.get(Calendar.DAY_OF_YEAR) - startdate.get(Calendar.DAY_OF_YEAR);
		return isBetween && (availabilityDistance == dayDistance) && 
				(availStartdate.get(Calendar.HOUR_OF_DAY) <= startdate.get(Calendar.HOUR_OF_DAY) 
					&& enddate.get(Calendar.HOUR_OF_DAY) <= availEnddate.get(Calendar.HOUR_OF_DAY)) 
				&& (availStartdate.get(Calendar.MINUTE) <= startdate.get(Calendar.MINUTE)
					&& enddate.get(Calendar.MINUTE) <= availEnddate.get(Calendar.MINUTE));
	}
	
	private Calendar getCalendar (Date date) {
		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		calendar.setTime(date);
		return calendar;
	}

}

package com.dvproject.vertTerm.Controller;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.dvproject.vertTerm.Model.Availability;
import com.dvproject.vertTerm.Model.Position;
import com.dvproject.vertTerm.Model.Procedure;
import com.dvproject.vertTerm.Model.ProcedureRelation;
import com.dvproject.vertTerm.Model.ResourceType;
import com.dvproject.vertTerm.Model.Restriction;
import com.dvproject.vertTerm.Service.ProcedureService;

@RestController
@RequestMapping("/Procedures")
@ResponseBody
public class ProcedureController {
	@Autowired
	private ProcedureService procedureService;
	
	@GetMapping("")
	public List<Procedure> getAllProcedures () {
		return procedureService.getAllProcedures();
	}
	
	@GetMapping("/{id}")
	public List<Procedure> getProcedure (@PathVariable String[] ids){
		return procedureService.getProcedures(ids);
	}
	
	@GetMapping("/{id}/PrecedingProcedure")
	public List<ProcedureRelation> getProcedurePrecedingProcedures (@PathVariable String id) {
		return procedureService.getPrecedingProcedures(id);
	}
	
	@GetMapping("/{id}/SubsequentProcedure")
	public List<ProcedureRelation> getProcedureSubsequentProcedures (@PathVariable String id) {
		return procedureService.getSubsequentProcedures(id);
	}
	
	@GetMapping("/{id}/ResourceTyp")
	public List<ResourceType> getProcedureResourceTypes (@PathVariable String id) {
		return procedureService.getNeededResourceTypes(id);
	}
	
	@GetMapping("/{id}/Position")
	public List<Position> getProcedurePosition (@PathVariable String id) {
		return procedureService.getNeededPositions(id);
	}
	
	@GetMapping("/{id}/Availability")
	public List<Availability> getProcedureAvailability (@PathVariable String id) {
		return procedureService.getAllAvailabilities(id);
	}
	
	@GetMapping("/{id}/Restriction")
	public List<Restriction> getProcedureRestrictions (@PathVariable String id) {
		return procedureService.getProcedureRestrictions(id);
	}
	
	@GetMapping("/{id}/isAvailable")
	public boolean isAvailable (@PathVariable String id,
			@RequestBody List<Date> dates) {
		return procedureService.isAvailableBetween(id, dates.get(0), dates.get(1));
	}
	
	@PostMapping("")
	public Procedure insertProcedure (@RequestBody Procedure procedure){
		return procedureService.insertProcedure(procedure);
	}

	@PutMapping("")
	public Procedure updateProceduredata (@RequestBody Procedure procedure) {
		return procedureService.updateProceduredata(procedure);
	}

	@PutMapping("/{id}/PrecedingProcedure")
	public List<ProcedureRelation> updateProcedurePrecedingProcedures (@PathVariable String id,
			@RequestBody List<ProcedureRelation> precedingProcedures) {
		return procedureService.updatePrecedingProcedures(id, precedingProcedures);
	}
	
	@PutMapping("/{id}/SubsequentProcedure")
	public List<ProcedureRelation> updateProcedureSubsequentProcedures (@PathVariable String id,
			@RequestBody List<ProcedureRelation> subsequentProcedures) {
		return procedureService.updateSubsequentProcedures(id, subsequentProcedures);
	}
	
	@PutMapping("/{id}/ResourceTyp")
	public List<ResourceType> updateProcedureResourceTypes (@PathVariable String id,
			@RequestBody List<ResourceType> resourceTypes) {
		return procedureService.updateNeededResourceTypes(id, resourceTypes);
	}
	
	@PutMapping("/{id}/Position")
	public List<Position> updateProcedurePosition (@PathVariable String id,
			@RequestBody List<Position> positions) {
		return procedureService.updateNeededPositions(id, positions);
	}
	
	@PutMapping("/{id}/Availability")
	public List<Availability> updateProcedureAvailability (@PathVariable String id,
			@RequestBody List<Availability> availabilities) {
		return procedureService.updateAllAvailabilities(id, availabilities);
	}
	
	@PutMapping("/{id}/Restriction")
	public List<Restriction> updateProcedureRestrictions (@PathVariable String id,
			@RequestBody List<Restriction> restrictions) {
		return procedureService.updateProcedureRestrictions(id, restrictions);
	}
	
	@DeleteMapping("/{id}")
	public boolean deleteProcedure (@PathVariable String id) {
		procedureService.deleteProcedure(id);
		return procedureService.isDeleted(id);
	}
}

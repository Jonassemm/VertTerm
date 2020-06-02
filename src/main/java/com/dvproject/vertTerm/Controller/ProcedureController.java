package com.dvproject.vertTerm.Controller;

import com.dvproject.vertTerm.Model.*;
import com.dvproject.vertTerm.Service.ProcedureService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/Procedures")
@ResponseBody
public class ProcedureController {
	@Autowired
	private ProcedureService procedureService;
	
	@GetMapping("")
	public List<Procedure> getAllProcedures() {
		return procedureService.getAll();
	}

	@GetMapping("/Status")
	public List<Procedure> getAllProceduresWithStatus(@RequestBody Status status) {
		return procedureService.getAll(status);
	}

	@GetMapping("/{id}")
	public List<Procedure> getProcedure(@PathVariable String id) {
		return procedureService.getByIds(new String[] { id });
	}

	@GetMapping("/")
	public List<Procedure> getProcedureRequestParam(@RequestParam String[] ids) {
		return procedureService.getByIds(ids);
	}

	@GetMapping("/{id}/PrecedingProcedure")
	public List<ProcedureRelation> getProcedurePrecedingProcedures(@PathVariable String id) {
		return procedureService.getPrecedingProcedures(id);
	}

	@GetMapping("/{id}/SubsequentProcedure")
	public List<ProcedureRelation> getProcedureSubsequentProcedures(@PathVariable String id) {
		return procedureService.getSubsequentProcedures(id);
	}

	@GetMapping("/{id}/ResourceTyp")
	public List<ResourceType> getProcedureResourceTypes(@PathVariable String id) {
		return procedureService.getNeededResourceTypes(id);
	}

	@GetMapping("/{id}/Position")
	public List<Position> getProcedurePosition(@PathVariable String id) {
		return procedureService.getNeededPositions(id);
	}

	@GetMapping("/{id}/Availability")
	public List<Availability> getProcedureAvailability(@PathVariable String id) {
		return procedureService.getAllAvailabilities(id);
	}

	@GetMapping("/{id}/Restriction")
	public List<Restriction> getProcedureRestrictions(@PathVariable String id) {
		return procedureService.getProcedureRestrictions(id);
	}

	@GetMapping("/{id}/isAvailable")
	public boolean isAvailable(
			@PathVariable String id, 
			@RequestParam Date startdate, 
			@RequestParam Date enddate) {
		return procedureService.isAvailableBetween(id, startdate, enddate);
	}

	@PostMapping("")
	public Procedure insertProcedure(@RequestBody Procedure procedure) {
		return procedureService.create(procedure);
	}

	@PutMapping("")
	public Procedure updateProcedure(@RequestBody Procedure procedure) {
		return procedureService.update(procedure);
	}

	@PutMapping("/{id}/Data")
	public Procedure updateProceduredata(
			@PathVariable String id, 
			@RequestBody Procedure procedure) {
		return procedureService.updateProceduredata(procedure);
	}

	@PutMapping("/{id}/PrecedingProcedure")
	public List<ProcedureRelation> updateProcedurePrecedingProcedures(
			@PathVariable String id,
			@RequestBody List<ProcedureRelation> precedingProcedures) {
		return procedureService.updatePrecedingProcedures(id, precedingProcedures);
	}

	@PutMapping("/{id}/SubsequentProcedure")
	public List<ProcedureRelation> updateProcedureSubsequentProcedures(
			@PathVariable String id,
			@RequestBody List<ProcedureRelation> subsequentProcedures) {
		return procedureService.updateSubsequentProcedures(id, subsequentProcedures);
	}

	@PutMapping("/{id}/ResourceTyp")
	public List<ResourceType> updateProcedureResourceTypes(
			@PathVariable String id,
			@RequestBody List<ResourceType> resourceTypes) {
		return procedureService.updateNeededResourceTypes(id, resourceTypes);
	}

	@PutMapping("/{id}/Position")
	public List<Position> updateProcedurePosition(
			@PathVariable String id, 
			@RequestBody List<Position> positions) {
		return procedureService.updateNeededPositions(id, positions);
	}

	@PutMapping("/{id}/Availability")
	public List<Availability> updateProcedureAvailability(
			@PathVariable String id,
			@RequestBody List<Availability> availabilities) {
		return procedureService.updateAvailabilities(id, availabilities);
	}

	@PutMapping("/{id}/Restriction")
	public List<Restriction> updateProcedureRestrictions(
			@PathVariable String id,
			@RequestBody List<Restriction> restrictions) {
		return procedureService.updateProcedureRestrictions(id, restrictions);
	}

	@DeleteMapping("/{id}")
	public boolean deleteProcedure(@PathVariable String id) {
		procedureService.delete(id);
		return procedureService.delete(id);
	}
}

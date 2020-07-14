package com.dvproject.vertTerm.Controller;

import com.dvproject.vertTerm.Model.*;
import com.dvproject.vertTerm.Service.AppointmentServiceImpl;
import com.dvproject.vertTerm.Service.ProcedureService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/Procedures")
@ResponseBody
public class ProcedureController {
	@Autowired
	private ProcedureService procedureService;
	@Autowired
	private AppointmentServiceImpl appointmentService;
	
	@GetMapping("")
	public List<Procedure> getAllProcedures () {
		return procedureService.getAll();
	}
	
	@GetMapping("/Status/{status}")
	public List<Procedure> getAllProceduresWithStatus (
			@PathVariable Status status,
			@RequestParam(required = false) Boolean publicProcedure) {
		return publicProcedure != null ? procedureService.getAll(status, publicProcedure) : procedureService.getAll(status);
	}
	
	@GetMapping("/{id}")
	public List<Procedure> getProcedure (@PathVariable String id){
		return procedureService.getByIds(new String [] {id});
	}
	
	@GetMapping("/")
	public List<Procedure> getProcedureRequestParam (@RequestParam String [] ids){
		return procedureService.getByIds(ids);
	}
	
	@GetMapping("/{id}/isAvailable")
	public boolean isAvailable (
			@PathVariable String id,
			@RequestParam Date startdate,
			@RequestParam Date enddate) {
		return procedureService.isAvailableBetween(id, startdate, enddate);
	}
	
	@PostMapping("")
	public Procedure insertProcedure (@RequestBody Procedure procedure){
		return procedureService.create(procedure);
	}

	@PutMapping("")
	public Procedure updateProcedure (@RequestBody Procedure procedure) {
		return procedureService.update(procedure);
	}
	
	@DeleteMapping("/{id}")
	public boolean deleteProcedure (@PathVariable String id) {
		procedureService.delete(id);
		return procedureService.delete(id);
	}
}

package com.dvproject.vertTerm.Controller;

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

import com.dvproject.vertTerm.Model.Consumable;
import com.dvproject.vertTerm.Model.Status;
import com.dvproject.vertTerm.Service.ConsumableService;

/**
 * @author Joshua Müller
 */
@RestController
@RequestMapping("/Consumables")
@ResponseBody
public class ConsumableController {
	@Autowired
	private ConsumableService consumableService;
	
	@GetMapping
	public List<Consumable> getAllConsumables (){
		return consumableService.getAll();
	}
	
	@GetMapping("/Status/{status}")
	public List<Consumable> getAllConsumablesWithStatus (@PathVariable Status status){
		return consumableService.getAllWithStatus(status);
	}
	
	@GetMapping("/{id}")
	public Consumable getConsumable (@PathVariable String id) {
		return consumableService.getById(id);
	}
	
	@PostMapping
	public Consumable insertConsumable (@RequestBody Consumable consumable) {
		return consumableService.create(consumable);
	}
	
	@PutMapping("/{id}")
	public Consumable updateConsumable (@PathVariable String id,@RequestBody Consumable consumable) {
		consumable.setId(id);
		return consumableService.update(consumable);
	}
	
	@DeleteMapping("/{id}")
	public boolean deleteConsumable (@PathVariable String id) {
		return consumableService.delete(id);
	}
}

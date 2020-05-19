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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.dvproject.vertTerm.Model.Position;
import com.dvproject.vertTerm.Service.PositionService;

@RestController
@RequestMapping("/Positions")
@ResponseBody
public class PositionController {
	@Autowired
	private PositionService positionService;
	
	@GetMapping
	public List<Position> getAllPositions () {
		return positionService.getAllPositions();
	}
	
	@GetMapping("/{id}")
	private List<Position> getPositions (@PathVariable String id) {
		return positionService.getPositions(new String [] {id});
	}
	
	@GetMapping("/")
	private List<Position> getPositionsRequestParameter (@RequestParam String [] ids) {
		return positionService.getPositions(ids);
	}
	
	@PostMapping
	private Position insertPosition (@RequestBody Position position) {
		return positionService.insertPosition(position);
	}
	
	@PutMapping
	private Position updatePosition (@RequestBody Position position) {
		return positionService.updatePosition(position);
	}
	
	@DeleteMapping("/{id}")
	private boolean deletePosition (@PathVariable String id) {
		return positionService.deletePosition(id);
	}

}

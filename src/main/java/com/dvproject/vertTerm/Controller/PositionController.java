package com.dvproject.vertTerm.Controller;

import com.dvproject.vertTerm.Model.Position;
import com.dvproject.vertTerm.Service.PositionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/Positions")
@ResponseBody
public class PositionController {
	@Autowired
	private PositionService positionService;
	
	@GetMapping
	public List<Position> getAllPositions () {
		return positionService.getAll();
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
		return positionService.create(position);
	}
	
	@PutMapping
	private Position updatePosition (@RequestBody Position position) {
		return positionService.update(position);
	}
	
	@DeleteMapping("/{id}")
	private boolean deletePosition (@PathVariable String id) {
		return positionService.delete(id);
	}

}

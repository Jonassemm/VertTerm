package com.dvproject.vertTerm.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.dvproject.vertTerm.Model.OpeningHours;
import com.dvproject.vertTerm.Service.OpeningHoursService;

@RestController
@RequestMapping("/OpeningHours")
@ResponseBody
public class OpeningHoursController {
	@Autowired
	private OpeningHoursService openingHoursService;
	
	@GetMapping("/")
	public OpeningHours getOpeningHours(){
		return openingHoursService.get();
	}
	
	@PutMapping("/")
	public OpeningHours updateOpeningHours(@RequestBody OpeningHours openingHours){
		return openingHoursService.update(openingHours);
	}
	
	@DeleteMapping("/")
	public void updateAvailabilitiesOfThePast(){
		openingHoursService.deleteAvailabilitiesInThePast();
	}

}

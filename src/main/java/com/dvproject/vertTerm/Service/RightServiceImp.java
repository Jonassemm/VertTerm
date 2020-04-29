package com.dvproject.vertTerm.Service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dvproject.vertTerm.Service.RightService;
import com.dvproject.vertTerm.Model.Right;
import com.dvproject.vertTerm.repository.RightRepository;


@Service
@Transactional
public class RightServiceImp implements RightService {

   @Autowired
   private RightRepository RightRepo;
    
 

   @Override
   public List <Right> getAllRights() {
       return this.RightRepo.findAll();
   }

   @Override
   public Right getRightById(String id) {
	   Optional <Right> RightDb = this.RightRepo.findById(id);
       if (RightDb.isPresent()) {
           return RightDb.get();
       } else {
           throw new ResourceNotFoundException("Record not found with id : " + id);
       }
   }
   
}




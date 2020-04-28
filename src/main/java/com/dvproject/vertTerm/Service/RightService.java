package com.dvproject.vertTerm.Service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dvproject.vertTerm.Model.Right;
import com.dvproject.vertTerm.repository.RightRepository;

@Service
public class RightService
{
    @Autowired
    private RightRepository rightRepository;
    
    public List<Right> getAllRights ()
    {
	List<Right> rights = rightRepository.findAll();
	return rights;
    }
    
    public List<Right> getRightsRightIds (String [] ids)
    {
	List<Right> rights = new ArrayList <Right> ();
	
	for (String id : ids)
	{
	    rights.add(rightRepository.findById(id).get());
	}
	
	return rights;
    }

}

package com.dvproject.vertTerm.Service;
import java.util.List;

import com.dvproject.vertTerm.Model.Right;

public interface RightService {

		
	    List<Right> getAllRights();
	    Right getRightById(String id);
	    
	
}

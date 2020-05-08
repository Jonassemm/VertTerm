package com.dvproject.vertTerm.Service;
import java.util.List;

import com.dvproject.vertTerm.Model.Right;
import com.dvproject.vertTerm.Model.Role;
import com.dvproject.vertTerm.Model.User;

public interface RightService {

		
	    List<Right> getAllRights();
	    Right getRightById(String id);
		List<User> getListUserswithRight(String id);
		List<Role> getListRoleswithRight(String id);    
}

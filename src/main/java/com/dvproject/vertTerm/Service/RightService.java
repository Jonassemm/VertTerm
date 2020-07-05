package com.dvproject.vertTerm.Service;
import com.dvproject.vertTerm.Model.Right;
import com.dvproject.vertTerm.Model.Role;
import com.dvproject.vertTerm.Model.User;

import java.util.List;

//author Amar Alkhankan
public interface RightService {

		
	    List<Right> getAll();
	    Right getById(String id);
		List<User> getListUserswithRight(String id);
		List<Role> getListRoleswithRight(String id);    
}

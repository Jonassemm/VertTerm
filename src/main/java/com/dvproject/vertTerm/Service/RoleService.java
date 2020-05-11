package com.dvproject.vertTerm.Service;

import java.util.List;

//import com.dvproject.vertTerm.Model.Right;
import com.dvproject.vertTerm.Model.Role;

public interface RoleService {
	
    Role createRole(Role role);

    Role updateRole(Role role);

    List<Role> getAllRoles();

    Role getRoleById(String id);

    void deleteRoleById(String id);
    
  
    
}

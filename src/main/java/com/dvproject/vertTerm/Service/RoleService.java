package com.dvproject.vertTerm.Service;

import java.util.List;

import com.dvproject.vertTerm.Model.Right;
import com.dvproject.vertTerm.Model.Role;
import com.dvproject.vertTerm.Model.User;

public interface RoleService {
	
    Role createRole(Role role);

    Role updateRole(Role role);

    List<Role> getAllRoles();

    Role getRoleById(String id);

    void deleteRoleById(String id);
    
    List<Right> getRoleRights(String id);
    
    Role updateRoleRights(Role role);
    
    List<User>  getRoleUsers(String id);
    
    //TODO
    // updateRoleUsers(String id);
 
}


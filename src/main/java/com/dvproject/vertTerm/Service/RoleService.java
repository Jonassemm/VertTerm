package com.dvproject.vertTerm.Service;

import java.util.List;

import com.dvproject.vertTerm.Model.ResourceType;
import com.dvproject.vertTerm.Model.Right;
import com.dvproject.vertTerm.Model.Role;
import com.dvproject.vertTerm.Model.User;

public interface RoleService {
	
    Role createRole(Role role);

    Role updateRole(Role role);

    List<Role> getAllRoles();

    Role getRoleById(String id);

    List<Role> getRoles(String[] ids);
    
    boolean deleteRoleById(String id);
    
    List<Right> getRoleRights(String id);
    
    Role updateRoleRights(Role role);
    
    List<User> getRoleUsers(String id);
    
    List<User> updateRoleUsers(String id,String[] Uids);
 
}


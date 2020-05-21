package com.dvproject.vertTerm.Service;

import com.dvproject.vertTerm.Model.Right;
import com.dvproject.vertTerm.Model.Role;
import com.dvproject.vertTerm.Model.User;

import java.util.List;

public interface RoleService extends Service<Role>{
    
    List<Right> getRoleRights(String id);
    
    Role updateRoleRights(Role role);
    
    List<User>  getRoleUsers(String id);
    
    //TODO
    // updateRoleUsers(String id);
 
}


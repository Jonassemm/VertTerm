package com.dvproject.vertTerm.Service;

import java.util.List;

import com.dvproject.vertTerm.Model.ResourceType;

import com.dvproject.vertTerm.Model.Right;
import com.dvproject.vertTerm.Model.Role;
import com.dvproject.vertTerm.Model.User;

import java.util.List;


    List<Role> getRoles(String[] ids);
    
    boolean deleteRoleById(String id);

public interface RoleService extends BasicService<Role> {

    
    List<Right> getRoleRights(String id);
    
    Role updateRoleRights(Role role);
    
    List<User> getRoleUsers(String id);
    
    List<User> updateRoleUsers(String id,String[] Uids);
 
}


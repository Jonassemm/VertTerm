package com.dvproject.vertTerm.Service;

import java.util.List;

import com.dvproject.vertTerm.Model.ResourceType;

import com.dvproject.vertTerm.Model.Right;
import com.dvproject.vertTerm.Model.Role;
import com.dvproject.vertTerm.Model.User;

import java.util.List;

/**  author Amar Alkhankan  **/
public interface RoleService extends BasicService<Role> {

	List<Role> getRoles(String[] ids);
	 
    List<Right> getRoleRights(String id);
    
    List<Right> updateRoleRights(String id,String[] Rids);
    
    List<User> getRoleUsers(String id);
    
    List<User> updateRoleUsers(String id,String[] Uids);
 
}


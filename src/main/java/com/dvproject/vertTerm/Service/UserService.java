package com.dvproject.vertTerm.Service;

import java.security.Principal;
import java.util.List;

import com.dvproject.vertTerm.Model.Right;
import com.dvproject.vertTerm.Model.Role;
import com.dvproject.vertTerm.Model.User;

public interface UserService
{
    List<User> getAllUsers();
    User getOwnUser(Principal principal);
    List<Role> getOwnUserRoles (Principal principal);
    List<Right> getOwnUserRights (Principal principal);
    List<User> getUsersWithUsernames (String [] usernames);
    List<User> getUsersWithIds (String [] ids);
    List<Role> getUserRolesWithId (String id);
    List<Right> getUserRightsWithId (String id);
}

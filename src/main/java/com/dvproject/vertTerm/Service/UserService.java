package com.dvproject.vertTerm.Service;

import com.dvproject.vertTerm.Model.Right;
import com.dvproject.vertTerm.Model.Role;
import com.dvproject.vertTerm.Model.Status;
import com.dvproject.vertTerm.Model.User;

import java.security.Principal;
import java.util.List;

public interface UserService extends BasicService<User> {
    User getOwnUser(Principal principal);
    List<User> getAll(Status status);
    List<Role> getOwnUserRoles (Principal principal);
    List<Right> getOwnUserRights (Principal principal);
    List<User> getUsersWithUsernames (String [] usernames);
    List<Role> getUserRolesWithId (String id);
    List<Right> getUserRightsWithId (String id);
    
    void encodePassword (User user);
    void testMandatoryFields(User user);
    User getAnonymousUser ();
}

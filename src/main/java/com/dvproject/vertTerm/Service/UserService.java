package com.dvproject.vertTerm.Service;

import com.dvproject.vertTerm.Model.Right;
import com.dvproject.vertTerm.Model.Role;
import com.dvproject.vertTerm.Model.Status;
import com.dvproject.vertTerm.Model.User;

import java.security.Principal;
import java.util.List;

/**
 * @author Robert Schulz
 */
public interface UserService extends BasicService<User> {
    User getOwnUser(Principal principal);
    List<User> getAll(Status status);
    
    /**
     * @author Joshua Müller
     */
    void encodePassword (User user);
    void testMandatoryFields(User user);
    void testAppointments(String userid);
    User getAnonymousUser ();
    void testWarningsFor(String userid);
}

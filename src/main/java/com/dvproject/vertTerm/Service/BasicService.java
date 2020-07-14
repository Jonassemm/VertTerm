package com.dvproject.vertTerm.Service;

import java.util.List;

/**
 * @author Robert Schulz
 */
public interface BasicService<T> {
    //GET
    List<T> getAll ();
    T getById (String id);

    //POST
    T create(T newInstance);

    //PUT
    T update(T updatedInstance);

    //DELETE
    boolean delete(String id);
}

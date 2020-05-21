package com.dvproject.vertTerm.Service;

import java.util.List;

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

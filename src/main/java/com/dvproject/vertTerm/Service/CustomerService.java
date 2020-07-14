package com.dvproject.vertTerm.Service;

import com.dvproject.vertTerm.Model.Customer;
import com.dvproject.vertTerm.Model.Employee;
import com.dvproject.vertTerm.Model.Status;
import com.dvproject.vertTerm.Model.User;

import java.util.List;

/**
 * @author Robert Schulz
 */
public interface CustomerService extends BasicService<Customer> {
    List<Customer> getAll(Status status);
}

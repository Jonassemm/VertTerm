package com.dvproject.vertTerm.Service;

import com.dvproject.vertTerm.Model.Customer;
import com.dvproject.vertTerm.Model.Employee;
import com.dvproject.vertTerm.Model.Status;

import java.util.List;

public interface CustomerService extends BasicService<Customer> {
    List<Customer> getAll(Status status);
}
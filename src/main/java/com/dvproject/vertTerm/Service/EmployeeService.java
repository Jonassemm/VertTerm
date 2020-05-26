package com.dvproject.vertTerm.Service;

import com.dvproject.vertTerm.Model.Employee;
import com.dvproject.vertTerm.Model.Status;
import com.dvproject.vertTerm.Model.User;

import java.util.List;

public interface EmployeeService extends BasicService<Employee>{
    List<Employee> getAll(Status status);
}

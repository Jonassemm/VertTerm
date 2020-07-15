package com.dvproject.vertTerm.Service;

import com.dvproject.vertTerm.Model.Availability;
import com.dvproject.vertTerm.Model.Employee;
import com.dvproject.vertTerm.Model.Position;
import com.dvproject.vertTerm.Model.Status;
import com.dvproject.vertTerm.Model.User;

import java.util.Date;
import java.util.List;

/**
 * @author Robert Schulz
 */
public interface EmployeeService extends BasicService<Employee>{
    List<Employee> getAll(String positionId);
    Employee getByUsername(String username);
    //List<Employee> getAll(Position position);
     List<Availability> getAllAvailabilities(String id);
     boolean isEmployeeAvailableBetween(String id, Date startdate, Date enddate);
     List<Employee>  getEmployeesByPositionIdandStatus(String positionId,Status status);
}

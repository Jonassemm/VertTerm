package com.dvproject.vertTerm.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.dvproject.vertTerm.Model.Employee;

public interface EmployeeRepository extends MongoRepository<Employee, String>
{
    @Query("{'_id' : ?0, '_class' : 'com.dvproject.vertTerm.Model.Employee'}")
    Optional<Employee> findById (String id);
    
    @Query("{'username' : ?0, '_class' : 'com.dvproject.vertTerm.Model.Employee'}")
    Employee findByUsername (String username);
}
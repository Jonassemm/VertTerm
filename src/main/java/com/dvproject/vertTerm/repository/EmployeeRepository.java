package com.dvproject.vertTerm.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.dvproject.vertTerm.Model.Employee;

public interface EmployeeRepository extends MongoRepository<Employee, String>{	
	static final String employeeTest = "'_class' : 'com.dvproject.vertTerm.Model.Employee'";
	
	/**
	 * @author Joshua Müller
	 */
	@Override
	@Query("{" + employeeTest + "}")
	List<Employee> findAll();

    /**
     * @author Joshua Müller
     */
    @Query("{'_id' : ?0, " + employeeTest + "}")
    Optional<Employee> findById (String id);
    
    /**
     * @author Joshua Müller
     */
    @Query("{'username' : ?0, " + employeeTest + "}")
    Optional<Employee> findByUsername (String username);
    
    /**
     * @author Joshua Müller
     */
    List<Employee> findByPositionsId(String id);
}

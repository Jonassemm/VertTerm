package com.dvproject.vertTerm.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.dvproject.vertTerm.Model.Employee;

public interface EmployeeRepository extends MongoRepository<Employee, String>{	
	static final String employeeTest = "'_class' : 'com.dvproject.vertTerm.Model.Employee'";
	
	@Override
	@Query("{" + employeeTest + "}")
	List<Employee> findAll();

	@Query("{'systemStatus' : 'ACTIVE', " + employeeTest + "}")
    List<Employee> findAllActive();

    @Query("{'systemStatus' : 'INACTIVE', " + employeeTest + "}")
    List<Employee> findAllInactive();

    @Query("{'systemStatus' : 'DELETED', " + employeeTest + "}")
    List<Employee> findAllDeleted();
	
    @Query("{'_id' : ?0, " + employeeTest + "}")
    Optional<Employee> findById (String id);
    
    @Query("{'username' : ?0, " + employeeTest + "}")
    Optional<Employee> findByUsername (String username);
    
    List<Employee> findByPositionsId(String id);
}

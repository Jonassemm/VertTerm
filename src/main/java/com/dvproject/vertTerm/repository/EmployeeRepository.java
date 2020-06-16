package com.dvproject.vertTerm.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.dvproject.vertTerm.Model.Employee;

public interface EmployeeRepository extends MongoRepository<Employee, String>{	
	@Override
	@Query("{'_class' : 'com.dvproject.vertTerm.Model.Employee'}")
	List<Employee> findAll();

	@Query("{'systemStatus' : 'ACTIVE', '_class' : 'com.dvproject.vertTerm.Model.Employee'}")
    List<Employee> findAllActive();

    @Query("{'systemStatus' : 'INACTIVE', '_class' : 'com.dvproject.vertTerm.Model.Employee'}")
    List<Employee> findAllInactive();

    @Query("{'systemStatus' : 'DELETED', '_class' : 'com.dvproject.vertTerm.Model.Employee'}")
    List<Employee> findAllDeleted();
	
    @Query("{'_id' : ?0, '_class' : 'com.dvproject.vertTerm.Model.Employee'}")
    Optional<Employee> findById (String id);
    
    @Query("{'username' : ?0, '_class' : 'com.dvproject.vertTerm.Model.Employee'}")
    Employee findByUsername (String username);
    
//    @Query("{'positions.$id' : ObjectId(?0), '_class' : 'com.dvproject.vertTerm.Model.Employee'}")
    List<Employee> findByPositionsId(String id);
}

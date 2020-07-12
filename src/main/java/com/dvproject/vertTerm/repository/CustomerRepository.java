package com.dvproject.vertTerm.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.dvproject.vertTerm.Model.Customer;

public interface CustomerRepository extends MongoRepository<Customer, String> {
	static final String customerTest = "'_class' : 'com.dvproject.vertTerm.Model.Customer'";
	
	/**
	 * @author Joshua Müller
	 */
	@Override
	@Query("{" + customerTest + "}")
	List<Customer> findAll();

    @Query("{'systemStatus' : 'ACTIVE', " + customerTest + "}")
    List<Customer> findAllActive();

    @Query("{'systemStatus' : 'INACTIVE', " + customerTest + "}")
    List<Customer> findAllInactive();

    @Query("{'systemStatus' : 'DELETED'," + customerTest + "}")
    List<Customer> findAllDeleted();
	
    /**
     * @author Joshua Müller
     */
    @Query("{'_id' : ?0, " + customerTest + "}")
    Optional<Customer> findById (String id);
    
    /**
     * @author Joshua Müller
     */
    @Query("{'username' : ?0, " + customerTest + "}")
    Customer findByUsername (String username);
}

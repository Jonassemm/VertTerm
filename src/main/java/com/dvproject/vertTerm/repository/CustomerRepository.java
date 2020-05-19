package com.dvproject.vertTerm.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.dvproject.vertTerm.Model.Customer;

public interface CustomerRepository extends MongoRepository<Customer, String>{
	@Override
	@Query("{'_class' : 'com.dvproject.vertTerm.Model.Customer'}")
	List<Customer> findAll();
	
    @Query("{'_id' : ?0, '_class' : 'com.dvproject.vertTerm.Model.Customer'}")
    Optional<Customer> findById (String id);
    
    @Query("{'username' : ?0, '_class' : 'com.dvproject.vertTerm.Model.Customer'}")
    Customer findByUsername (String username);
}

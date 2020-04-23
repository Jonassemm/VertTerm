package com.dvproject.vertTerm.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.dvproject.vertTerm.Model.User;

@RepositoryRestResource(collectionResourceRel = "people", path = "people")
public interface UserRespository extends MongoRepository<User, String> 
{
	List<User> findByLastName(@Param("name") String name);
	
	User findByUsername(@Param("username") String username);
}

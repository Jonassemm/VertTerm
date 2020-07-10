package com.dvproject.vertTerm.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.dvproject.vertTerm.Model.Role;

/**
 * @author Joshua Müller
 */
public interface RoleRepository extends MongoRepository<Role, String> {
	Role findByName(String name);

	Optional<Role> findById(String id);

	Role save(Role entity);
}

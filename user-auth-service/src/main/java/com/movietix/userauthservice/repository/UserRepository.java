package com.movietix.userauthservice.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.movietix.userauthservice.model.User;

public interface UserRepository extends JpaRepository<User, String> {
	boolean existsByEmail(String email);

	Optional<User> findByEmail(String email);
}

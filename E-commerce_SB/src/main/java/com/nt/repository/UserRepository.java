package com.nt.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nt.entity.Role;
import com.nt.entity.User;

public interface UserRepository extends JpaRepository<User, Long>{

	public Optional<User> findByUsername(String username);

	public boolean existsByUsername(String username);

	public boolean existsByEmail(String email);

	
}

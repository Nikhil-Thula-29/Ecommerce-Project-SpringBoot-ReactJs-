package com.nt.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nt.entity.Role;
import com.nt.entity.User;

public interface UserRepository extends JpaRepository<User, Long>{

	public Optional<User> findByUserName(String username);

	public boolean existsByUserName(String username);

	public boolean existsByEmail(String email);

	
}

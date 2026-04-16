package com.nt.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nt.entity.AppRole;
import com.nt.entity.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {

	public Optional<Role> findByRoleName(AppRole appRole);

	
}

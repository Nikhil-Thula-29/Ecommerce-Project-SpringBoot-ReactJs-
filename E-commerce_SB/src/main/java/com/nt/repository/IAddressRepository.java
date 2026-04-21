package com.nt.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nt.entity.Address;

public interface IAddressRepository extends JpaRepository<Address, Long>{

}

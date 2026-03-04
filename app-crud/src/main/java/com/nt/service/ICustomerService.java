package com.nt.service;

import java.util.List;


import com.nt.entity.Customer;

public interface ICustomerService {

	public String createCustomer(Customer customer);
	
	public List<Customer> getAllCustomers();
	
	public Customer getCustomerById(Long id);
	
	public Customer updateCustomer(Long id,Customer customer);
	
	public String deleteCustomerById(Long id);
	
}

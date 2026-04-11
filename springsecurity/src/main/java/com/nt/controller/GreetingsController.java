package com.nt.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GreetingsController {

	@GetMapping("/hello")
	public String sayHello() {
		return "Hello";
	}
	
	@PreAuthorize("hasRole('USER')") //need to use the role from security config file only.
	@GetMapping("/user")
	public String userEndPoint() {
		return "User End Point";
	}
	
	
	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/admin")
	public String adminEndpoint() {
		return "Admin End Point";
	}
}

package com.nt.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nt.security.JwtUtils;
import com.nt.security.LoginRequest;
import com.nt.security.LoginResponse;

@RestController
@RequestMapping("/api")
public class GreetingsController {
	
	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private JwtUtils jwtUtils;
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
	
	
	@PostMapping("/signin")
	public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest){
		Authentication authentication;
		try {
			authentication=authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(
					loginRequest.getUsername(),
					loginRequest.getPassword()
					)
					);
		}catch(AuthenticationException expection) {
			Map<String,Object> map=new HashMap<>();
			map.put("message", "Bad Credentials");
			map.put("status", false);
			return new ResponseEntity<Object>(map,HttpStatus.NOT_FOUND);
		}
		SecurityContextHolder.getContext().setAuthentication(authentication);
		UserDetails userDetails=(UserDetails) authentication.getPrincipal();
		String jwtToken=jwtUtils.generateTokenFromUserName(userDetails);
		List<String> roles=userDetails.getAuthorities().stream()
				.map(role->role.getAuthority())
				.collect(Collectors.toList());
		LoginResponse response=new LoginResponse(jwtToken,userDetails.getUsername(),roles);
		return ResponseEntity.ok(response);
	}
}

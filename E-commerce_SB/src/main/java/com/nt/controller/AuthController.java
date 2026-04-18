package com.nt.controller;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nt.entity.AppRole;
import com.nt.entity.Role;
import com.nt.entity.User;
import com.nt.repository.RoleRepository;
import com.nt.repository.UserRepository;
import com.nt.security.jwt.JwtUtils;
import com.nt.security.jwt.LoginRequest;
import com.nt.security.jwt.MessageResponse;
import com.nt.security.jwt.SignupRequest;
import com.nt.security.jwt.UserInfoResponse;
import com.nt.security.services.UserDetailsImpl;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

	@Autowired
	private JwtUtils jwtUtils;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private RoleRepository roleRepository;

	@PostMapping("/signin")
	public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
		Authentication authentication;
		try {
			authentication = authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
		} catch (AuthenticationException expection) { //if we write here only directly exception is handled it will show response from here only
			Map<String, Object> map = new HashMap<>();
			map.put("message", "Bad Credentials");
			map.put("status", false);
			return new ResponseEntity<Object>(map, HttpStatus.UNAUTHORIZED);
		}
		SecurityContextHolder.getContext().setAuthentication(authentication);
		UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
		ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(userDetails);
		List<String> roles = userDetails.getAuthorities().stream().map(role -> role.getAuthority())
				.collect(Collectors.toList());
		UserInfoResponse response = new UserInfoResponse(userDetails.getId(), userDetails.getUsername(),
				roles);
		return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
				.body(response);
	}

	@PostMapping("/signup")
	public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signupRequest) {
		if (userRepository.existsByUserName(signupRequest.getUsername())) {
			return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken!"));
		}
		if (userRepository.existsByEmail(signupRequest.getEmail())) {
			return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already taken!"));
		}
		User user = new User(signupRequest.getUsername(), signupRequest.getEmail(),
				passwordEncoder.encode(signupRequest.getPassword()));

		Set<String> strRoles = signupRequest.getRoles();
		Set<Role> roles = new HashSet<>();
		//by default it will be user
		if (strRoles == null || strRoles.isEmpty()) {
			Role userRole = roleRepository.findByRoleName(AppRole.ROLE_USER)
					.orElseThrow(() -> new RuntimeException("Error: Role is not found"));
			roles.add(userRole);
		} else {
			strRoles.forEach(role -> {
				switch (role.toLowerCase()) {
				case "admin": { //if admin
					Role adminRole = roleRepository.findByRoleName(AppRole.ROLE_ADMIN)
							.orElseThrow(() -> new RuntimeException("Error: Role is not found"));
					roles.add(adminRole);
					break;
				}
				case "seller": {//if seller
					Role sellerRole = roleRepository.findByRoleName(AppRole.ROLE_SELLER)
							.orElseThrow(() -> new RuntimeException("Error: Role is not found"));
					roles.add(sellerRole);
					break;
				}
				default://if nothing like user types "admin234" some thing not there in that case we assing user by default
					Role userRole = roleRepository.findByRoleName(AppRole.ROLE_USER)
							.orElseThrow(() -> new RuntimeException("Error: Role is not found"));
					roles.add(userRole);
					break;
				}
			});
		}
		user.setRoles(roles);
		userRepository.save(user);
		return ResponseEntity.ok(new MessageResponse("User Registred Successfully.."));
	}
	
	@GetMapping("/username")
	public String currentUserName(Authentication authentication) {
		if(authentication!=null) {
			return authentication.getName();
		}else {
			return "";
		}
	}
	
	@GetMapping("/userdetails")
	public ResponseEntity<?> currentUserDetails(Authentication authentication) {
		UserDetailsImpl userDetails=(UserDetailsImpl) authentication.getPrincipal();
		List<String> roles=userDetails.getAuthorities().stream().map(role->role.getAuthority()).collect(Collectors.toList());
		UserInfoResponse response = new UserInfoResponse(userDetails.getId(), userDetails.getUsername(),
				roles);
		return ResponseEntity.ok().body(response);
	}
	
	
	@PostMapping("/signout")
	public ResponseEntity<?> signoutUser(){
		ResponseCookie cookie=jwtUtils.getCleanJwtCookie();
		return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString())
				.body(new MessageResponse("You 've been signed out!"));
	}

}

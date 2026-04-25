package com.nt.util;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.nt.entity.User;
import com.nt.repository.UserRepository;



@Component
public class AuthUtil {
	
	@Autowired
	private UserRepository userRepository;

	public String loggedInEmail() {
		Authentication authentication=SecurityContextHolder.getContext().getAuthentication();
		Optional<User> user=userRepository.findByUsername(authentication.getName());
		if(user.isPresent()) {
			return user.get().getEmail();
		}else {
			throw new UsernameNotFoundException("User Not Found with username: "+user.get().getUsername());
		}
	}
	
	public Long loggedInUserId() {
		Authentication authentication=SecurityContextHolder.getContext().getAuthentication();
		Optional<User> user=userRepository.findByUsername(authentication.getName());
		if(user.isPresent()) {
			return user.get().getUserId();
		}else {
			throw new UsernameNotFoundException("User Not Found with username: "+user.get().getUserId());
		}
	}
	
	public User loggedInUser() {
		Authentication authentication=SecurityContextHolder.getContext().getAuthentication();
		Optional<User> user=userRepository.findByUsername(authentication.getName());
		if(user.isPresent()) {
			return user.get();
		}else {
			throw new UsernameNotFoundException("User Not Found!");
		}
	}
}

package com.nt.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.nt.models.SocialUser;
import com.nt.service.ISocialService;

@RestController
public class SocialController {
	
	@Autowired
	private ISocialService socialService;

	@GetMapping("/social/users")
	public ResponseEntity<List<SocialUser>> getUsers(){
		return new ResponseEntity<List<SocialUser>>(socialService.getAllUsers(),HttpStatus.OK);
	}
	
	@PostMapping("/social/users")
	public ResponseEntity<SocialUser> saveUsers(@RequestBody SocialUser socialUser){
		return new ResponseEntity<SocialUser>(socialService.saveUsers(socialUser),HttpStatus.CREATED);
	}
	
	@DeleteMapping("/social/users/{id}")
	public ResponseEntity<String> deleteUser(@PathVariable(name="id") Long id){
		String msg=socialService.deleteUser(id);
		return new ResponseEntity<String>(msg,HttpStatus.OK);
	}
}

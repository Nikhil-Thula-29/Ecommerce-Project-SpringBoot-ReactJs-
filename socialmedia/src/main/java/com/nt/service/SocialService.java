package com.nt.service;

import java.util.List;
import java.util.Optional;

import javax.management.RuntimeErrorException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nt.models.SocialUser;
import com.nt.repository.ISocialUserRepository;

@Service
public class SocialService implements ISocialService{
	
	@Autowired
	private ISocialUserRepository userRepo;

	@Override
	public List<SocialUser> getAllUsers() {
		List<SocialUser> users=userRepo.findAll();
		return users;
	}

	@Override
	public SocialUser saveUsers(SocialUser socialUser) {
		SocialUser user= userRepo.save(socialUser);
		return user;
	}

	@Override
	public String deleteUser(Long id) {
		Optional<SocialUser> user=userRepo.findById(id);
		if(user.isPresent()) {
			userRepo.delete(user.get());
			return "Deleted successfully";
		}else {
			throw new RuntimeException("User Not Found with Id "+id);
		}
	}

	
}

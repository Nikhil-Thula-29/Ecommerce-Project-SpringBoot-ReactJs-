package com.nt.service;

import java.util.List;


import com.nt.models.SocialUser;

public interface ISocialService {

	
	public List<SocialUser> getAllUsers();

	public  SocialUser saveUsers(SocialUser socialUser);

	public String deleteUser(Long id);

}

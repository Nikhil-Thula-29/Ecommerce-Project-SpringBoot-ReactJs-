package com.nt.config;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.nt.models.SocialGroup;
import com.nt.models.SocialPosts;
import com.nt.models.SocialProfile;
import com.nt.models.SocialUser;
import com.nt.repository.ISocialGroupsRepository;
import com.nt.repository.ISocialPostsRepository;
import com.nt.repository.ISocialProfileRepository;
import com.nt.repository.ISocialUserRepository;

@Configuration
public class DataInitializer {
	
	@Autowired
	private ISocialUserRepository userRepo;
	private ISocialProfileRepository profileRepo;
	private ISocialPostsRepository postRepo;
	private ISocialGroupsRepository groupRepo;
	
	

	public DataInitializer(ISocialUserRepository userRepo, ISocialProfileRepository profileRepo,
			ISocialPostsRepository postRepo, ISocialGroupsRepository groupRepo) {
		super();
		this.userRepo = userRepo;
		this.profileRepo = profileRepo;
		this.postRepo = postRepo;
		this.groupRepo = groupRepo;
	}



	@Bean
	public CommandLineRunner mydata() {
		return (args->{
			// Create some users
			SocialUser user1=new SocialUser();
			SocialUser user2=new SocialUser();
			SocialUser user3=new SocialUser();
			
			 // Save users to the database
			userRepo.save(user1);
			userRepo.save(user2);
			userRepo.save(user3);
			
			// Create some groups
			SocialGroup groups1=new SocialGroup();
			SocialGroup groups2=new SocialGroup();
			 // Add users to groups
			groups1.getSocialUser().add(user1);
			groups1.getSocialUser().add(user2);
			
			groups2.getSocialUser().add(user2);
			groups2.getSocialUser().add(user3);
			
			 // Save groups to the database
			groupRepo.save(groups1);
			groupRepo.save(groups2);
			
			// Associate users with groups
			user1.getGroups().add(groups1);
			user2.getGroups().add(groups1);
			user2.getGroups().add(groups2);
			user3.getGroups().add(groups2);
			
			 // Save users back to database to update associations
			userRepo.save(user1);
			userRepo.save(user2);
			userRepo.save(user3);
			
			 // Create some posts
			SocialPosts socialPosts1=new SocialPosts();
			SocialPosts socialPosts2=new SocialPosts();
			SocialPosts socialPosts3=new SocialPosts();

			 // Associate posts with users
			socialPosts1.setSocialUser(user1);
			socialPosts2.setSocialUser(user2);
			socialPosts3.setSocialUser(user3);
			
			// Save posts to the database (assuming you have a PostRepository)
			postRepo.save(socialPosts1);
			postRepo.save(socialPosts2);
			postRepo.save(socialPosts3);
			
			// Create some social profiles
			SocialProfile prof1=new SocialProfile();
			SocialProfile prof2=new SocialProfile();
			SocialProfile prof3=new SocialProfile();
			
			// Associate profiles with users
			prof1.setSocialUser(user1);
			prof2.setSocialUser(user2);
			prof3.setSocialUser(user3);
			
			// Save profiles to the database (assuming you have a SocialProfileRepository)
			profileRepo.save(prof1);
			profileRepo.save(prof2);
			profileRepo.save(prof3);
			
		});
		
		
	}
	
	
}

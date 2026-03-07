package com.nt.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nt.models.SocialGroup;
import com.nt.models.SocialPosts;
import com.nt.models.SocialProfile;
import com.nt.models.SocialUser;

public interface ISocialUserRepository extends JpaRepository<SocialUser,Long>{

}

package com.nt.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nt.models.SocialGroup;
import com.nt.models.SocialPosts;
import com.nt.models.SocialProfile;

public interface ISocialProfileRepository extends JpaRepository<SocialProfile,Long>{

}

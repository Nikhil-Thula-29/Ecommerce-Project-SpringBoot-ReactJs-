package com.nt.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nt.models.SocialGroup;
import com.nt.models.SocialPosts;

public interface ISocialPostsRepository extends JpaRepository<SocialPosts,Long>{

}

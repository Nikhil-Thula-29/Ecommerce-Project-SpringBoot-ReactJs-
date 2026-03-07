package com.nt.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SocialProfile {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@OneToOne
	@JoinColumn(name="social_user")
	@JsonIgnore  //to over come Jackson (JSON converter) is going into an infinite loop while converting your entities to JSON this is due to bidirectional
	//we need to write jsonignore because of bidirectional mapping and write in other class like not in user because it is main class where we map all other classes so wirte @jsonignore in other classes.
	private SocialUser socialUser;
	
	
	public void setSocialUser(SocialUser socialUser) {
	    this.socialUser = socialUser;

	    if (socialUser != null && socialUser.getSocialProfile() != this) {
	        socialUser.setSocialProfile(this);
	    }
	}
}

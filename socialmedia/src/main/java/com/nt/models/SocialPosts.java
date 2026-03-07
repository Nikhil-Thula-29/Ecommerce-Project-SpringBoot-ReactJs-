package com.nt.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SocialPosts {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@ManyToOne
	@JoinColumn(name="user_id") //here joincolumn is used for representing as fk i.e pk in socialUser table.
	@JsonIgnore  //to over come Jackson (JSON converter) is going into an infinite loop while converting your entities to JSON this is due to bidirectional
	//we need to write jsonignore because of bidirectional mapping and write in other class like not in user because it is main class where we map all other classes so wirte @jsonignore in other classes.
	private SocialUser socialUser;

	
}

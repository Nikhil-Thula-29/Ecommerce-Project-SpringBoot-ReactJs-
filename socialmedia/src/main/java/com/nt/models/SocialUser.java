package com.nt.models;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.annotations.ManyToAny;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;

@Entity
public class SocialUser {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	
	@OneToOne(mappedBy = "socialUser")
	//@JoinColumn(name="social_profile")
	private SocialProfile socialProfile;
	
	@OneToMany(mappedBy = "socialUser")//Imp: check whether mappedBy has to match socialUser that has to match SocialPosts field name.
	private List<SocialPosts> posts=new ArrayList<>();
	
	@ManyToMany
	@JoinTable(
			name="user_group",//table name
			joinColumns=@JoinColumn(name="user_id"),//pk of present entity for new table it is FK
			inverseJoinColumns=@JoinColumn(name="group_id")//Pk of other entity for new table it is FK , but in other onetoone in that if we write @joincolumn it means there we are using pk of other entity but here we are using present entity. check example down
			)
	private Set<SocialGroup> groups=new HashSet<SocialGroup>();
	
	//in general we wont write referencedColumnName for pk joining but if we are using other field as fk then we use referencedColumnName, if we use pk internally it calls referencedColname with id so need again if other field only then we use referencedColname.
	/*@OneToOne
	@JoinColumn(
	    name="user_email",
	    referencedColumnName="email"
	)
	private SocialUser socialUser;*/
	
	//example of join column- other entity pk as fk of that table
	/*@ManyToOne
	@JoinColumn(name="user_id")   //here it is representing pk of Socialuser
	private SocialUser socialUser;*/
	
}

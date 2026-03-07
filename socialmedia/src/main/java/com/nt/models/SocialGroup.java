package com.nt.models;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
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
public class SocialGroup {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@ManyToMany(mappedBy = "groups")
	@JsonIgnore  //to over come Jackson (JSON converter) is going into an infinite loop while converting your entities to JSON this is due to bidirectional
	//we need to write jsonignore because of bidirectional mapping and write in other class like not in user because it is main class where we map all other classes so wirte @jsonignore in other classes.
	private Set<SocialUser> socialUser=new HashSet<SocialUser>();
	
	@Override
	public int hashCode() {
	    return Objects.hash(id);
	}

	@Override
	public boolean equals(Object obj) {
	    if (this == obj) return true;
	    if (!(obj instanceof SocialGroup)) return false;
	    SocialGroup other = (SocialGroup) obj;
	    return Objects.equals(id, other.id);
	}
}

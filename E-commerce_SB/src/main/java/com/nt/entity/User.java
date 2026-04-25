package com.nt.entity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.EqualsAndHashCode.Exclude;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name="users",
       uniqueConstraints = {
    		   @UniqueConstraint(columnNames = "username"),
    		   @UniqueConstraint(columnNames = "email")
       })
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="user_id")
	private Long userId;
	
	@NotBlank
	@Size(max=20)
	@Column(name="username")
	private String username;
	
	@NotBlank
	@Size(max=50)
	@Email
	@Column(name="email")
	private String email;
	
	@NotBlank
	@Size(max=120)
	@Column(name="password")
	private String password;
	
	
	
	
	@Getter
	@Setter
	@Exclude
	@ToString.Exclude
	@ManyToMany(cascade = {CascadeType.MERGE},fetch = FetchType.LAZY)
	@JoinTable(name="user_role",
			joinColumns = @JoinColumn(name="user_id"),
			inverseJoinColumns = @JoinColumn(name="role_id"))
	private Set<Role> roles=new HashSet<>();
	
	
	//This is for seller point of view as a user
	//user can have multiple products to sell so one to many i.e one user can sell multiple products
	//mappedby="user" so not mapped here bcz already user mapped in that product table only this pk is created but this can help in fetching all products with userid
	@Exclude
	@ToString.Exclude //we can write because it is mappedby user we can exclude in tostring.
	@OneToMany(mappedBy = "user",cascade = {CascadeType.PERSIST,CascadeType.MERGE},orphanRemoval = true) //orphanRemoval means "If a child is removed from parent collection → delete it from DB" and cascade.delete is different->"If parent is deleted → delete all children"
	private Set<Product> products = new HashSet<>();;
	
	@Getter
	@Setter
	@Exclude //added not to include equals hashcode etc by lombok
	@ToString.Exclude
	@OneToMany(mappedBy = "user" ,cascade = {CascadeType.PERSIST,CascadeType.MERGE},orphanRemoval = true)
//	@JoinTable(name="user_address",
//			joinColumns = @JoinColumn(name="user_id"),
//			inverseJoinColumns = @JoinColumn(name="address_id"))
	private List<Address> addresses=new ArrayList<>();
	
	//Bidirectional
	@Exclude
	@ToString.Exclude
	@OneToOne(mappedBy ="user",cascade = {CascadeType.PERSIST,CascadeType.REMOVE,CascadeType.MERGE},orphanRemoval = true)
	private Cart cart;

	public User(@NotBlank @Size(max = 20) String username, @NotBlank @Size(max = 50) @Email String email,
			@NotBlank @Size(max = 120) String password) {
		super();
		this.username = username;
		this.email = email;
		this.password = password;
	}
	
}

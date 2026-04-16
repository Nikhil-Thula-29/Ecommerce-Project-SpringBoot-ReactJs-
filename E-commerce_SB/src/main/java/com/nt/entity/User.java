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
	private String userName;
	
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
	@ManyToMany(cascade = {CascadeType.PERSIST,CascadeType.MERGE},fetch = FetchType.EAGER)
	@JoinTable(name="user_role",
			joinColumns = @JoinColumn(name="user_id"),
			inverseJoinColumns = @JoinColumn(name="role_id"))
	private Set<Role> roles=new HashSet<>();
	
	
	//This is for seller point of view as a user
	//user can have multiple products to sell so one to many i.e one user can sell multiple products
	//mappedby="user" so not mapped here bcz already user mapped in that product table only this pk is created but this can help in fetching all products with userid
	@ToString.Exclude //we can write because it is mappedby user we can exclude in tostring.
	@OneToMany(mappedBy = "user",cascade = {CascadeType.PERSIST,CascadeType.MERGE},orphanRemoval = true) //orphanRemoval means "If a child is removed from parent collection → delete it from DB" and cascade.delete is different->"If parent is deleted → delete all children"
	private Set<Product> products;
	
	@Getter
	@Setter
	@ManyToMany(cascade = {CascadeType.PERSIST,CascadeType.MERGE})
	@JoinTable(name="user_address",
			joinColumns = @JoinColumn(name="user_id"),
			inverseJoinColumns = @JoinColumn(name="address_id"))
	private List<Address> addresses=new ArrayList<>();

	public User(@NotBlank @Size(max = 20) String userName, @NotBlank @Size(max = 50) @Email String email,
			@NotBlank @Size(max = 120) String password) {
		super();
		this.userName = userName;
		this.email = email;
		this.password = password;
	}
	
}

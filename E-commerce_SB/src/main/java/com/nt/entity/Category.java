package com.nt.entity;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "categories")
public class Category {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long categoryId;
	@NotBlank(message="Name cannot be empty")
	@Size(min = 5,message="Category name must contain atleast 5 characters")
	private String categoryName;
	
	@OneToMany(mappedBy = "category",cascade = CascadeType.ALL)
	private List<Product> product;
}

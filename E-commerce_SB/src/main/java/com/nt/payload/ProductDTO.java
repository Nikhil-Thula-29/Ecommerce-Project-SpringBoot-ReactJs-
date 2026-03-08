package com.nt.payload;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


//Do not keep @entity on DTO Classes.
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDTO {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long productId;
	private String productName;
	private String image;
	private Integer quantity;
	private String description;
	private double price;
	private double discount;
	private double specialPrice;
	
}

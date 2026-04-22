package com.nt.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


//Do not keep @entity on DTO Classes.
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDTO {

	private Long productId;
	private String productName;
	private String image;
	private Integer quantity;
	private String description;
	private double price;
	private double discount;
	private double specialPrice;
	
}

package com.nt.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name="cart_items")
@Entity
public class CartItem {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long cartItemId;
	
	@ManyToOne//no cascade because we write it in parent class only i.e if we remove cartitem then cart should not delete
	@JoinColumn(name="cart_id")
	private Cart cart;
	
	@ManyToOne//many carts can have same product so that's why many to one.
	@JoinColumn(name="product_id")
	private Product product;
	
	private Integer quantity;
	
	private Double discount;
	private Double productPrice;
	
}

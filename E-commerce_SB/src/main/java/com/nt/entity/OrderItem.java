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

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name="orders_items")
public class OrderItem {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long orderItemId;
	
	@ManyToOne //Many orderItems can be same products
	@JoinColumn(name="product_id")
	private Product product;
	
	@ManyToOne //Many orderItems can be in single order 
	@JoinColumn(name="order_id")
	private Order order;
	
	private Integer qunatity;
	private Double discount;
	private Double orderedProductPrice;
	
}

package com.nt.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name="products")
@ToString
public class Product {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long productId;
	private String productName;
	private String image;
	private String description;
	private Integer quantity;
	private double price; 
	private double discount;
	private double specialPrice;
	
	
	@ManyToOne
	@JoinColumn(name="category_id")
	private Category category;

	@ManyToOne
	@JoinColumn(name="seller_id")//it is user_id only name can be any
	private User user;
	
	//Bidirectional
	//It is parent for cartItem because if we delete product then that has to delete from cartitem also but if we delete cartitem then product should not delete
	//wrote mappedby because already in cartitem we mapped product so need in this table.
	@OneToMany(mappedBy = "product",cascade = {CascadeType.PERSIST,CascadeType.MERGE},fetch = FetchType.EAGER) //wrote cascade because this parent for cartItem.
	private List<CartItem> cartItems=new ArrayList<CartItem>();
}

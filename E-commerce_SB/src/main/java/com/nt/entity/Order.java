package com.nt.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name="orders")
public class Order {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long orderId;
	
	@Email
	@Column(nullable = false)
	private String email;
	
	@OneToMany(mappedBy = "order",cascade = {CascadeType.PERSIST,CascadeType.MERGE}) //one cart can have multiple orderItems
	private List<OrderItem> orderItems=new ArrayList<>();
	
	@OneToOne
	@JoinColumn(name="payment_id")
	private Payment payment;
	
	private LocalDate orderDate;
	
	private Double totalAmount;
	private String orderStatus;
	
	@ManyToOne //Many orders can have same(one) address 
	@JoinColumn(name="address_id")
	private Address address;
}

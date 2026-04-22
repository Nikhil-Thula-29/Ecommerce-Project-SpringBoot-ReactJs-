package com.nt.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name="payments")
public class Payment {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long paymentId;
	
	@OneToOne(mappedBy = "payment",cascade = {CascadeType.PERSIST,CascadeType.MERGE})
	private Order order;
	
	@NotBlank
	@Size(min=4,message="Payment must method must containn at least 4 characters")
	private String paymentMethod;
	
	private String pgPaymentId; //this is payment Id in gateway above id is application id.pg=paymentgateway
	private String pgStatus;
	private String pgResponseMessage;
	private String pgName;
	public Payment(Long paymentId,
			@NotBlank @Size(min = 4, message = "Payment must method must containn at least 4 characters") String paymentMethod,
			String pgPaymentId, String pgStatus, String pgResponseMessage, String pgName) {
		super();
		this.paymentId = paymentId;
		this.paymentMethod = paymentMethod;
		this.pgPaymentId = pgPaymentId;
		this.pgStatus = pgStatus;
		this.pgResponseMessage = pgResponseMessage;
		this.pgName = pgName;
	}
	
	
}

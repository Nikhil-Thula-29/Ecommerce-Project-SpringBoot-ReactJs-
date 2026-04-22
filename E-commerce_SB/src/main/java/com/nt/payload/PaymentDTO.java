package com.nt.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentDTO {
	private Long paymentId;
	private String paymentMethod;
	private String pgPaymentId; //this is payment Id in gateway above id is application id.pg=paymentgateway
	private String pgStatus;
	private String pgResponseMessage;
	private String pgName;
}

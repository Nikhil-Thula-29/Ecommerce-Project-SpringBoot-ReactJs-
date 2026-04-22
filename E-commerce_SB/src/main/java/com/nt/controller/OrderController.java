package com.nt.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nt.payload.OrderDTO;
import com.nt.payload.OrderRequestDTO;
import com.nt.service.OrderService;
import com.nt.util.AuthUtil;

@RestController
@RequestMapping("/api")
public class OrderController {
	
	@Autowired
	private OrderService orderService;
	
	@Autowired
	private AuthUtil authUtil;

	@PostMapping("/order/users/payments/{paymentMethod}")
	public ResponseEntity<OrderDTO> orderProducts(@PathVariable(name="paymentMethod") String paymentMethod,@RequestBody OrderRequestDTO orderRequestDTO){
		String email=authUtil.loggedInEmail();
		OrderDTO order=orderService.placeOrder(email,paymentMethod,orderRequestDTO);
		return new ResponseEntity<OrderDTO>(order,HttpStatus.CREATED);
	}
}

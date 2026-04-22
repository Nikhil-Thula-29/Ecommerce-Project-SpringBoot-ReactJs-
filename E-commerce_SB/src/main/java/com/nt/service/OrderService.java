package com.nt.service;

import com.nt.payload.OrderDTO;
import com.nt.payload.OrderRequestDTO;

public interface OrderService {

	public OrderDTO placeOrder(String email, String paymentMethod, OrderRequestDTO orderRequestDTO);

}

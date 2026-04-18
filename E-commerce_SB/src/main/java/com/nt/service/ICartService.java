package com.nt.service;


import com.nt.payload.CartDTO;

public interface ICartService {
	
	public CartDTO addProductToCart(Long productId,Integer quantity);

}

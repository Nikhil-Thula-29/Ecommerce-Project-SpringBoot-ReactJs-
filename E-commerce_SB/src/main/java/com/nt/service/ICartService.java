package com.nt.service;


import java.util.List;

import com.nt.payload.CartDTO;

public interface ICartService {
	
	public CartDTO addProductToCart(Long productId,Integer quantity);

	public List<CartDTO> getAllCarts();

	public CartDTO getCart(String emailId, Long cartId);

	public CartDTO updateProductQuantityInCart(Long productId, Integer quantity);

	public String deleteProductFromCart(Long cartId, Long productId);

	public void updateProductInCarts(Long cartId, Long productId);

}

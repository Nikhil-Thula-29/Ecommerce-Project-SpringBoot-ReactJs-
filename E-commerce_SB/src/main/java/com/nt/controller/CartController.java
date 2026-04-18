package com.nt.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nt.payload.CartDTO;
import com.nt.service.ICartService;

@RestController
@RequestMapping("/api")
public class CartController {
	
	@Autowired
	private ICartService cartService;

	@PostMapping("/carts/products/{productId}/quantity/{quantity}")
	public ResponseEntity<CartDTO> addProductToCart(@PathVariable("productId") Long productId,@PathVariable("quantity") Integer quantity){
		CartDTO cartDTO=cartService.addProductToCart(productId,quantity);
		return new ResponseEntity<CartDTO>(cartDTO,HttpStatus.CREATED);
	}
}

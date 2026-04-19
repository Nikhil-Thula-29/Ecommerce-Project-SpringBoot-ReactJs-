package com.nt.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nt.entity.Cart;
import com.nt.exception.ResourceNotFoundException;
import com.nt.payload.CartDTO;
import com.nt.repository.CartRepository;
import com.nt.service.ICartService;
import com.nt.util.AuthUtil;

@RestController
@RequestMapping("/api")
public class CartController {
	
	@Autowired
	private ICartService cartService;
	
	@Autowired
	private CartRepository cartRepository; 
	
	@Autowired
	private AuthUtil authUtil;

	@PostMapping("/carts/products/{productId}/quantity/{quantity}")
	public ResponseEntity<CartDTO> addProductToCart(@PathVariable("productId") Long productId,@PathVariable("quantity") Integer quantity){
		CartDTO cartDTO=cartService.addProductToCart(productId,quantity);
		return new ResponseEntity<CartDTO>(cartDTO,HttpStatus.CREATED);
	}
	
	//This is getting details of all users cart list of carts.
	@GetMapping("/carts")
	public ResponseEntity<List<CartDTO>> getCarts(){
		List<CartDTO> cartDTO=cartService.getAllCarts();
		return new ResponseEntity<List<CartDTO>>(cartDTO,HttpStatus.FOUND);
	}
	
	
	//This is getting detail of specific user cart.
	@GetMapping("/carts/users/cart")
	public ResponseEntity<CartDTO> getCartById(){
		String emailId=authUtil.loggedInEmail();
		Cart cart=cartRepository.findCartByEmail(emailId);
		if (cart == null) {
	        throw new ResourceNotFoundException("Cart", "email", emailId);
	    }
		Long cartId=cart.getCartId();
		CartDTO cartDTO=cartService.getCart(emailId,cartId); //here we can use any one like email or cartId to fetch details of cart of particular user but for future scalibility purpose we are taking both.
		return new ResponseEntity<CartDTO>(cartDTO,HttpStatus.OK);
	}
	
	
	@PutMapping("/cart/products/{productId}/quantity/{operation}")
	public ResponseEntity<CartDTO> updateCartProduct(@PathVariable("productId") Long productId,@PathVariable("operation") String operation){ //in our case writing this ("operation"),("product") at every @pathVariable are mandatory without that giving error
		CartDTO cartDTO= cartService.updateProductQuantityInCart(productId,operation.equalsIgnoreCase("delete")?-1:1);
		return new ResponseEntity<CartDTO>(cartDTO,HttpStatus.OK);
	}
	
	@DeleteMapping("/carts/{cartId}/product/{productId}")
	public ResponseEntity<String> deleteProductFromCart(@PathVariable("cartId") Long cartId,@PathVariable("productId") Long productId){
		String status=cartService.deleteProductFromCart(cartId,productId);
		return new ResponseEntity<String>(status,HttpStatus.OK);
	}
}

package com.nt.service;

import java.util.List;
import java.util.stream.Stream;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nt.entity.Cart;
import com.nt.entity.CartItem;
import com.nt.entity.Product;
import com.nt.exception.APIException;
import com.nt.exception.ResourceNotFoundException;
import com.nt.payload.CartDTO;
import com.nt.payload.ProductDTO;
import com.nt.repository.CartItemRepository;
import com.nt.repository.CartRepository;
import com.nt.repository.IProductRepository;
import com.nt.util.AuthUtil;

@Service
public class CartServiceImpl implements ICartService {
	@Autowired
	private CartRepository cartRepository; 
	
	@Autowired
	private IProductRepository productRepository;
	
	@Autowired
	private CartItemRepository cartItemRepository;
	
	@Autowired
	private ModelMapper modelMapper;
	
	@Autowired
	private AuthUtil authUtil; //AuthUtil is a class which is used get all the authenticated details so that can be used in all projects globally.

	@Override
	public CartDTO addProductToCart(Long productId, Integer quantity) {
		Cart cart=createCart();
		
		//2. Retrive product details
		Product product=productRepository.findById(productId)
				.orElseThrow(()->new ResourceNotFoundException("Product", "productId", productId));
		
		//3. perform validations
		CartItem cartItem= cartItemRepository.findCartItemByProductIdAndCartId(cart.getCartId(),productId);
		if(cartItem!=null) {//Item alredy exists
			throw new APIException("Product "+product.getProductName()+" alrady exists in the cart");
		}
		
		if(product.getQuantity()==0) {
			throw new APIException(product.getProductName()+" is not available");
		}
		
		if(product.getQuantity()<quantity) {
			throw new APIException("Please make an order of the "+product.getProductName()+ " less or equal to the quantity "+product.getQuantity());
		}
		
		//4. Create cart Item
		CartItem newCartItem=new CartItem();
		newCartItem.setProduct(product);
		newCartItem.setCart(cart);
		newCartItem.setQuantity(quantity);
		newCartItem.setDiscount(product.getDiscount());
		newCartItem.setProductPrice(product.getSpecialPrice());
		
		//5. save cart item
		cartItemRepository.save(newCartItem);
		product.setQuantity(product.getQuantity());
		cart.setTotalPrice(cart.getTotalPrice()+(product.getSpecialPrice()*quantity));
		cartRepository.save(cart);
		//6.return updated cart Item
		CartDTO cartDTO=modelMapper.map(cart, CartDTO.class);
		
		//This step is doing because we have list of products in cartDTO
		//Here we are fetching List of cartItems from cart and we are looping one by one cartItem and getting product which is there in cartItem and that product is setting in productDTO 
		//but the issue is quantity is setting from product only which is wrong i.e existing items but we need how many items added in cart so we need to set explicity by item.getquantity()
		List<CartItem> cartItems=cart.getCartItems();
		Stream<ProductDTO> productStream=cartItems.stream().map(item->{
			ProductDTO map=modelMapper.map(item.getProduct(),ProductDTO.class);
			map.setQuantity(item.getQuantity());
			return map;
		});
		cartDTO.setProducts(productStream.toList());
		return cartDTO;
	}

	
	public Cart createCart() {
		//1. Find existing cart or create one
		//here we are getting email which is in user and user is in cart i.e (cart->user->email) that's why we are writing query jpa will not get query from sub layer.
		//Check this in CartRepository
		Cart userCart=cartRepository.findCartByEmail(authUtil.loggedInEmail());
		if(userCart!=null) {
			return userCart;
		}
		Cart cart =new Cart();
		cart.setTotalPrice(0.0);
		cart.setUser(authUtil.loggedInUser());
		Cart newCart=cartRepository.save(cart);
		return newCart;
	}
	
}

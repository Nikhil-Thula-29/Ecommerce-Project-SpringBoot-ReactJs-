package com.nt.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
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

import jakarta.transaction.Transactional;

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
		Cart cart = createCart();

		//2. Retrive product details
		Product product = productRepository.findById(productId)
				.orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

		//3. perform validations
		CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(cart.getCartId(), productId);
		if (cartItem != null) {//Item alredy exists
			throw new APIException("Product " + product.getProductName() + " already exists in the cart");
		}

		if (product.getQuantity() == 0) {
			throw new APIException(product.getProductName() + " is not available");
		}

		if (product.getQuantity() < quantity) {
			throw new APIException("Please make an order of the " + product.getProductName()
					+ " less or equal to the quantity " + product.getQuantity());
		}

		//4. Create cart Item
		CartItem newCartItem = new CartItem();
		newCartItem.setProduct(product);
		newCartItem.setCart(cart);
		newCartItem.setQuantity(quantity);
		newCartItem.setDiscount(product.getDiscount());
		newCartItem.setProductPrice(product.getSpecialPrice());

		//5. save cart item
		cartItemRepository.save(newCartItem);
		product.setQuantity(product.getQuantity());

		cart.getCartItems().add(newCartItem); //imp
		cart.setTotalPrice(cart.getTotalPrice() + (product.getSpecialPrice() * quantity));
		cartRepository.save(cart);
		//6.return updated cart Item
		CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);

		//This step is doing because we have list of products in cartDTO
		//Here we are fetching List of cartItems from cart and we are looping one by one cartItem and getting product which is there in cartItem and that product is setting in productDTO 
		//but the issue is quantity is setting from product only which is wrong i.e existing items but we need how many items added in cart so we need to set explicity by item.getquantity()
		List<CartItem> cartItems = cart.getCartItems();
		Stream<ProductDTO> productStream = cartItems.stream().map(item -> {
			ProductDTO map = modelMapper.map(item.getProduct(), ProductDTO.class);
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
		Cart userCart = cartRepository.findCartByEmail(authUtil.loggedInEmail());
		if (userCart != null) {
			return userCart;
		}
		Cart cart = new Cart();
		cart.setTotalPrice(0.0);
		cart.setUser(authUtil.loggedInUser()); //here we are setting the user to add to that particular user cart
		Cart newCart = cartRepository.save(cart);
		return newCart;
	}

	// Convert CartItems → ProductDTO list
	// Cart contains CartItems (not direct Products)
	//Reference comments for this part of code List<ProductDTO> products=cart.getCartItems().stream().map(prod->{
	/*ProductDTO produ=modelMapper.map(prod.getProduct(), ProductDTO.class);
	produ.setQuantity(prod.getQuantity());
	return produ; //rem imp to set produ in products.
	}).collect(Collectors.toList());
	cartDTO.setProducts(products);
	return cartDTO;*/
	// For each CartItem:
	// 1. Get actual Product → item.getProduct()
	// 2. Map Product → ProductDTO
	// 3. Override quantity → set cart quantity (NOT DB stock)
	//because Product.quantity = available stock
	//but we need quantity added in cart

	// IMPORTANT:
	// Must return ProductDTO inside map()
	// otherwise stream will not collect values

	// Final result:
	// List<ProductDTO> representing products in cart with correct quantity
	@Override
	public List<CartDTO> getAllCarts() {
		List<Cart> carts = cartRepository.findAll();
		if (carts.size() == 0) {
			throw new APIException("No cart exists!");
		}
		List<CartDTO> cartDTOs = carts.stream().map(cart -> {
			CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);
			List<ProductDTO> products = cart.getCartItems().stream().map(prod -> {
				ProductDTO produ = modelMapper.map(prod.getProduct(), ProductDTO.class);
				produ.setQuantity(prod.getQuantity());
				return produ; //rem imp to set produ in products.
			}).collect(Collectors.toList());
			cartDTO.setProducts(products);
			return cartDTO; //rem return imp to set cartDTO in cartDTOs.
		}).collect(Collectors.toList());
		return cartDTOs;
	}

	@Override
	public CartDTO getCart(String emailId, Long cartId) {
		Cart cart = cartRepository.findCartByEmailAndCartId(emailId, cartId);
		if (cart == null) {
			throw new ResourceNotFoundException("Cart", "cartId", cartId);
		}
		CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);
		List<ProductDTO> productDTOs = cart.getCartItems().stream().map(items -> {
			ProductDTO prod = modelMapper.map(items.getProduct(), ProductDTO.class);
			prod.setQuantity(items.getQuantity());
			return prod;
		}).collect(Collectors.toList());
		cartDTO.setProducts(productDTOs);
		return cartDTO;
	}

	@Transactional
	@Override
	public CartDTO updateProductQuantityInCart(Long productId, Integer quantity) {
		String email = authUtil.loggedInEmail();
		Cart userCart = cartRepository.findCartByEmail(email);
		Long cartId = userCart.getCartId();
		Cart cart = cartRepository.findById(cartId)
				.orElseThrow(() -> new ResourceNotFoundException("Cart", "cartId", cartId));
		Product product = productRepository.findById(productId)
				.orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

		if (product.getQuantity() == 0) {
			throw new APIException(product.getProductName() + " is not available");
		}


		CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(cartId,productId);
		if (cartItem == null) {
			throw new APIException("Product " + product.getProductName() + " not available in the cart!!!");
		}
		if (product.getQuantity() <= cartItem.getQuantity()) { //checks not to add extra product available in stock
			throw new APIException("Please make an order of the " + product.getProductName()
					+ " less or equal to the quantity " + product.getQuantity());
		}
		//calculate new quantity
		int newQuantity = cartItem.getQuantity() + quantity;
		//validation to prevent negative quantities
		if (newQuantity < 0) {
			throw new APIException("The resulting quantity cannot be negative.");
		}

		if (newQuantity == 0) {
			deleteProductFromCart(cartId, productId);
		} else {
			cartItem.setProductPrice(product.getSpecialPrice());
			cartItem.setQuantity(cartItem.getQuantity() + quantity);
			cartItem.setDiscount(product.getDiscount());
			cartItem.setCart(cart);
			cart.setTotalPrice(cart.getTotalPrice() + (cartItem.getProductPrice() * quantity));
			cartRepository.save(cart);
		}
		CartItem updatedItem = cartItemRepository.save(cartItem);
		if (updatedItem.getQuantity() == 0) {
			cartItemRepository.deleteById(updatedItem.getCartItemId());
		}
		CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);
		List<ProductDTO> productDTOs = cart.getCartItems().stream().map(item -> {
			ProductDTO prod = modelMapper.map(item.getProduct(), ProductDTO.class);
			prod.setQuantity(item.getQuantity());
			return prod;
		}).collect(Collectors.toList());
		cartDTO.setProducts(productDTOs);
		return cartDTO;
	}

	@Transactional
	@Override
	public String deleteProductFromCart(Long cartId, Long productId) {
		Cart cart = cartRepository.findById(cartId)
				.orElseThrow(() -> new ResourceNotFoundException("Cart", "cartId", cartId));
		CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(cartId, productId);
		if (cartItem == null) {
			throw new ResourceNotFoundException("Product", "productId", productId);
		}
		cart.setTotalPrice(cart.getTotalPrice() - (cartItem.getProductPrice() * cartItem.getQuantity()));
		cartItemRepository.deleteCartItemByProductIdAndCartId(cartId, productId);
		return "Product " + cartItem.getProduct().getProductName() + " removed from the cart !!!";
	}

	//used in updating the product price when updated in product service, this is used in ProductService.
	@Transactional
	@Override
	public void updateProductInCarts(Long cartId, Long productId) {
		Cart cart = cartRepository.findById(cartId)
				.orElseThrow(() -> new ResourceNotFoundException("Cart", "cartId", cartId));
		Product product = productRepository.findById(productId)
				.orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));
		CartItem cartItem=cartItemRepository.findCartItemByProductIdAndCartId(cartId, productId);
		if(cartItem==null) {
			throw new APIException("Product "+product.getProductName()+" not availabe in the cart!!!");
		}
		
		//1000-100*2=800 (1000 is already price in cart means cost of multiple products from that we removing updated product price completely)
		double cartPrice=cart.getTotalPrice()-(cartItem.getProductPrice()*cartItem.getQuantity());
		//increased 100 to 200 for 1 product
		cartItem.setProductPrice(product.getSpecialPrice());//setting the new price
		//800+200*2=1200
		cart.setTotalPrice(cartPrice+(cartItem.getProductPrice()*cartItem.getQuantity()));
		cartItem.setDiscount(product.getDiscount());
		cartItem=cartItemRepository.save(cartItem);
		//Here we are not saving cart then how totalprice is getting set then it is because of In JPA, updates should rely on dirty checking within a transactional context instead of explicitly calling save."
		//JPA automatically checks and save if required. (cart.setTotalPrice(cartPrice+(cartItem.getProductPrice()*cartItem.getQuantity()));)
	}

}

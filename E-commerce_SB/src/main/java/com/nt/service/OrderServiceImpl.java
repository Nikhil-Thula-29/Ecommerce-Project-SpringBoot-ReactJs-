package com.nt.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nt.entity.Address;
import com.nt.entity.Cart;
import com.nt.entity.CartItem;
import com.nt.entity.Order;
import com.nt.entity.OrderItem;
import com.nt.entity.Payment;
import com.nt.entity.Product;
import com.nt.exception.APIException;
import com.nt.exception.ResourceNotFoundException;
import com.nt.payload.OrderDTO;
import com.nt.payload.OrderItemDTO;
import com.nt.payload.OrderRequestDTO;
import com.nt.payload.PaymentDTO;
import com.nt.payload.ProductDTO;
import com.nt.repository.CartRepository;
import com.nt.repository.IAddressRepository;
import com.nt.repository.IProductRepository;
import com.nt.repository.OrderItemRepository;
import com.nt.repository.OrderRepository;
import com.nt.repository.PaymentRepository;

import jakarta.transaction.Transactional;

//we are not fetching product details here because we consider products from cart only so not added products in orderRequestDTO
@Service
public class OrderServiceImpl implements OrderService {

	@Autowired
	private CartRepository cartRepository;

	@Autowired
	private IAddressRepository addressRepository;

	@Autowired
	private PaymentRepository paymentRepository;

	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private OrderItemRepository orderItemRepository;

	@Autowired
	private IProductRepository productRepository;

	@Autowired
	private ICartService cartService;

	@Autowired
	private ModelMapper modelMapper;

	@Override
	@Transactional
	public OrderDTO placeOrder(String email, String paymentMethod, OrderRequestDTO orderRequestDTO) {
		//Getting User cart
		Cart cart = cartRepository.findCartByEmail(email);
		if (cart == null) {
			throw new ResourceNotFoundException("Cart", "email", email);
		}

		Address address = addressRepository.findById(orderRequestDTO.getAddressId()).orElseThrow(
				() -> new ResourceNotFoundException("Address", "addressId", orderRequestDTO.getAddressId()));
		//create new order with payment info
		Order order = new Order();
		order.setEmail(email);
		order.setOrderDate(LocalDate.now());
		order.setTotalAmount(cart.getTotalPrice());
		order.setOrderStatus("Order Accepted !");
		order.setAddress(address);

		Payment payment = new Payment();
		payment.setPaymentMethod(paymentMethod);
		payment.setPgName(orderRequestDTO.getPgName());
		payment.setPgPaymentId(orderRequestDTO.getPgPaymentId());
		payment.setPgResponseMessage(orderRequestDTO.getPgResponseMessage());
		payment.setPgStatus(orderRequestDTO.getPgStatus());
		payment.setOrder(order); //bidirectional 
		payment = paymentRepository.save(payment);

		order.setPayment(payment); //bidirectional 
		Order savedOrder = orderRepository.save(order);

		//Get items from the cart into the orderItems
		List<CartItem> cartItems = cart.getCartItems();
		if (cartItems.isEmpty()) {
			throw new APIException("Cart is empty!");
		}
		List<OrderItem> orderItems = new ArrayList<>();
		for (CartItem cartItem : cartItems) {
			OrderItem orderItem = new OrderItem();
			orderItem.setDiscount(cartItem.getDiscount());
			orderItem.setProduct(cartItem.getProduct());
			orderItem.setOrderedProductPrice(cartItem.getProductPrice());
			orderItem.setOrder(savedOrder);
			orderItem.setQunatity(cartItem.getQuantity());
			orderItems.add(orderItem);
		}
		orderItems = orderItemRepository.saveAll(orderItems);
		//Post order steps

		//Update the product stock
		List<CartItem> items = new ArrayList<>(cart.getCartItems()); //to save from ConcurrentModificationException we are declaring size before only because we are deleting cart size may vary.
		items.forEach(item -> {
		    int quantity = item.getQuantity();
		    Product product = item.getProduct();

		    product.setQuantity(product.getQuantity() - quantity);
		    productRepository.save(product);

		    //clear the cart
		    cartService.deleteProductFromCart(cart.getCartId(), item.getProduct().getProductId());
		});

		//send back the order summary
		OrderDTO orderDTO = modelMapper.map(savedOrder, OrderDTO.class);
		List<OrderItemDTO> orderItemsDTOs = orderItems.stream().map(item -> {
			OrderItemDTO orderItemDTO = new OrderItemDTO();
			orderItemDTO.setOrderId(item.getOrder().getOrderId());
			orderItemDTO.setDiscount(item.getDiscount());
			orderItemDTO.setOrderProductPrice(item.getOrderedProductPrice());
			Product prod = item.getProduct();
			orderItemDTO.setProduct(modelMapper.map(prod, ProductDTO.class));
			orderItemDTO.setQuantity(item.getQunatity());
			return orderItemDTO;
		}).collect(Collectors.toList());
		orderDTO.setOrderItems(orderItemsDTOs);
		PaymentDTO paymetDTO=modelMapper.map(payment, PaymentDTO.class);
		orderDTO.setPaymentDTO(paymetDTO);
		orderDTO.setAddressId(orderRequestDTO.getAddressId());
		return orderDTO;
	}

}

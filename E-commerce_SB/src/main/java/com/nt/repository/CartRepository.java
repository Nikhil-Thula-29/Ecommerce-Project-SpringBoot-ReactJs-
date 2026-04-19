package com.nt.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.nt.entity.Cart;

public interface CartRepository extends JpaRepository<Cart, Long>{
	
	//SELECT c.* FROM cart c JOIN user u ON c.user_id = u.id WHERE u.email = 'abc@gmail.com';SELECT c.* FROM cart c JOIN user u ON c.user_id = u.id WHERE u.email = 'abc@gmail.com';
	@Query("SELECT c FROM Cart c Where c.user.email=?1")//here we are getting email which is in user and user is in cart i.e (cart->user->email) that's why we are writing query jpa will not get query from sub layer.
	public Cart findCartByEmail(String email);

	@Query("SELECT c From Cart c Where c.user.email=?1 AND c.cartId=?2")
	public Cart findCartByEmailAndCartId(String emailId, Long cartId);

	//Cart → List<CartItem> → Product
	@Query("SELECT c from Cart c JOIN FETCH c.cartItems ci JOIN FETCH ci.product p where p.productId=?1")
	public List<Cart> findCartsByProductId(Long productId);

}

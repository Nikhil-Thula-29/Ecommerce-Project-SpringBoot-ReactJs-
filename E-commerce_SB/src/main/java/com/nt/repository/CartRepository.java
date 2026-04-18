package com.nt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.nt.entity.Cart;

public interface CartRepository extends JpaRepository<Cart, Long>{
	
	@Query("SELECT c FROM Cart c Where c.user.email=?1")//here we are getting email which is in user and user is in cart i.e (cart->user->email) that's why we are writing query jpa will not get query from sub layer.
	public Cart findCartByEmail(String email);

}

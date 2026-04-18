package com.nt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.nt.entity.CartItem;

//Generally repo returns normal enity but in our case Iservice return DTO class.
public interface CartItemRepository extends JpaRepository<CartItem, Long>{

	//Rem query of syntax how to write.(ci.cart.cartId)
	@Query("Select ci from cartItem ci where ci.cart.cartId=?1 And ci.product.productId=?2")
	public CartItem findCartItemByProductIdAndCartId(Long cartId, Long productId);

}

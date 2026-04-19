package com.nt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.nt.entity.CartItem;

//Generally repo returns normal enity but in our case Iservice return DTO class.
public interface CartItemRepository extends JpaRepository<CartItem, Long>{

	//Rem query of syntax how to write.(ci.cart.cartId)
	@Query("Select ci from CartItem ci where ci.cart.cartId=?1 And ci.product.productId=?2") //Rem always use entity name i.e with caps as CartItem not the db name in JPA.
	public CartItem findCartItemByProductIdAndCartId(Long cartId,Long productId);

	@Modifying
	@Query("DELETE FROM CartItem ci where ci.cart.cartId=?1 AND ci.product.productId=?2")
	public void deleteCartItemByProductIdAndCartId(Long cartId, Long productId);
}

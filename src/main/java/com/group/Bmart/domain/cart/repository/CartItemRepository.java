package com.group.Bmart.domain.cart.repository;

import com.group.Bmart.domain.cart.Cart;
import com.group.Bmart.domain.cart.CartItem;
import com.group.Bmart.domain.user.User;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CartItemRepository extends JpaRepository<CartItem,Long> {
    List<CartItem> findAllByCartItemIdOrderByCreatedAt(Long cartItemId);

    List<CartItem> findAllByCartOrderByCreatedAt(Cart cart);

    @Modifying
    @Query("delete from CartItem ci"
            + " where ci.cart ="
            + " (select c from Cart c where c.user = :user)")
    void deleteByUser(@Param("user") User user);
}

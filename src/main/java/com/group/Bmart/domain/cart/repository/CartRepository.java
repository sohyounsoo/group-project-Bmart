package com.group.Bmart.domain.cart.repository;

import com.group.Bmart.domain.cart.Cart;
import com.group.Bmart.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart,Long> {
    Optional<Cart> findByUser(final User user);
    void deleteByUser(User user);
}

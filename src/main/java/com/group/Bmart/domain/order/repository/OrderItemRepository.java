package com.group.Bmart.domain.order.repository;

import com.group.Bmart.domain.item.Item;
import com.group.Bmart.domain.order.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    Optional<OrderItem> findByItem(Item item);

    @Query("SELECT SUM(oi.quantity) "
        + "FROM OrderItem oi "
        + "WHERE oi.item.id = :itemId")
    Long countByOrderItemId(@Param("itemId") Long itemId);
}

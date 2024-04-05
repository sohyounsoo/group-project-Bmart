package com.group.Bmart.domain.delivery.repository;

import com.group.Bmart.domain.delivery.Delivery;
import com.group.Bmart.domain.order.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface DeliveryRepository extends JpaRepository<Delivery, Long> {

    @Query("select d from Delivery d join fetch d.order where d.order.orderId = :orderId")
    Optional<Delivery> findByOrderIdWithOrder(@Param("orderId") Long orderId);

    boolean existsByOrder(Order order);
}

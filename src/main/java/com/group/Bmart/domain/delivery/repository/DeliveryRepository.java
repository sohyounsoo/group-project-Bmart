package com.group.Bmart.domain.delivery.repository;

import com.group.Bmart.domain.delivery.Delivery;
import com.group.Bmart.domain.order.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeliveryRepository extends JpaRepository<Delivery, Long> {


    boolean existsByOrder(Order order);
}

package com.group.Bmart.domain.order.service.response;

import com.group.Bmart.domain.order.Order;

public record CreateOrderResponse(
    Long orderId,
    String name,
    Integer totalPrice,
    String address,
    Integer deliveryFee

) {

    public static CreateOrderResponse from(Order order) {
        return new CreateOrderResponse(
            order.getOrderId(),
            order.getName(),
            order.getPrice(),
            order.getAddress(),
            order.getDeliveryFee()
        );
    }
}

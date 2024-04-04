package com.group.Bmart.domain.delivery.service.request;

public record RegisterDeliveryCommand(Long orderId, Long userId, int estimateMinutes) {

    public static RegisterDeliveryCommand of(
            final Long orderId,
            final Long userId,
            final int estimateMinutes) {
        return new RegisterDeliveryCommand(orderId, userId, estimateMinutes);
    }
}

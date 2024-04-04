package com.group.Bmart.domain.delivery;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum DeliveryStatus {
    ACCEPTING_ORDER,
    START_DELIVERY,
    DELIVERED;
}

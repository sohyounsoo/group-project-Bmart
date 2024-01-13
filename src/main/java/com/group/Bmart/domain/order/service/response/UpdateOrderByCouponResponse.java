package com.group.Bmart.domain.order.service.response;

import com.group.Bmart.domain.coupon.Coupon;
import com.group.Bmart.domain.order.Order;

public record UpdateOrderByCouponResponse(
    Integer totalPrice,
    Integer discountPrice
) {

    public static UpdateOrderByCouponResponse of(final Order order, final Coupon coupon) {
        return new UpdateOrderByCouponResponse(order.getPrice(),
            coupon.getDiscount());
    }
}

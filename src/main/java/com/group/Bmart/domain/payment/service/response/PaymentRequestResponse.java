package com.group.Bmart.domain.payment.service.response;

public record PaymentRequestResponse(
    Integer amount,
    String orderId,
    String orderName,
    String customerEmail,
    String customerName,
    String successUrl,
    String failUrl
) {

}

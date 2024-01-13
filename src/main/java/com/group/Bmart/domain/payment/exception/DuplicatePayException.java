package com.group.Bmart.domain.payment.exception;

public class DuplicatePayException extends PaymentException {

    public DuplicatePayException(final String message) {
        super(message);
    }
}

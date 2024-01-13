package com.group.Bmart.domain.order.exception;

public abstract class OrderException extends RuntimeException {

    public OrderException(String message) {
        super(message);
    }
}

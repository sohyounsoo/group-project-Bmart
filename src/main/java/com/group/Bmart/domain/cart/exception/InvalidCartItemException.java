package com.group.Bmart.domain.cart.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class InvalidCartItemException extends CartException{
    public InvalidCartItemException(String message) {
        super(message);
    }
}

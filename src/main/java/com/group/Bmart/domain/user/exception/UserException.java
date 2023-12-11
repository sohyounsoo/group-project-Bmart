package com.group.Bmart.domain.user.exception;

public abstract class UserException extends RuntimeException {

    protected UserException(final String message) {
        super(message);
    }
}

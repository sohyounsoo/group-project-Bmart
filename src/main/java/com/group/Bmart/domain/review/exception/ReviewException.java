package com.group.Bmart.domain.review.exception;

public abstract class ReviewException extends RuntimeException {

    public ReviewException(final String message) {
        super(message);
    }
}

package com.group.Bmart.domain.user.service.request;

public record FindUserCommand(Long userId) {

    public static FindUserCommand from(Long userId) {
        return new FindUserCommand(userId);
    }
}

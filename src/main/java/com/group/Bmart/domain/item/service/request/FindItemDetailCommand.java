package com.group.Bmart.domain.item.service.request;

public record FindItemDetailCommand(Long itemId) {

    public static FindItemDetailCommand from(final Long itemId) {
        return new FindItemDetailCommand(itemId);
    }
}

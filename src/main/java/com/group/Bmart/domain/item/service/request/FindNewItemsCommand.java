package com.group.Bmart.domain.item.service.request;

import com.group.Bmart.domain.item.ItemSortType;
import org.springframework.data.domain.PageRequest;

import java.util.Objects;

public record FindNewItemsCommand(
    Long lastIdx,
    Long lastItemId,
    PageRequest pageRequest,
    ItemSortType sortType) {

    private static final int DEFAULT_PAGE_NUMBER = 0;

    public static FindNewItemsCommand of(Long lastIdx, Long lastItemId, int pageSize,
        String sortType) {
        if (isFirstIdx(lastIdx)) {
            lastIdx = Long.parseLong(String.valueOf(Integer.MAX_VALUE));
            if (Objects.equals(sortType, "LOWEST_AMOUNT") || Objects.equals(sortType, "NEW")) {
                lastIdx = Long.MIN_VALUE;
            }
        }

        if (isFirstIdx(lastItemId)) {
            lastItemId = Long.parseLong(String.valueOf(Integer.MAX_VALUE));
        }

        ItemSortType itemSortType = ItemSortType.from(sortType);
        PageRequest pageRequest = PageRequest.of(DEFAULT_PAGE_NUMBER, pageSize);
        return new FindNewItemsCommand(lastIdx, lastItemId, pageRequest, itemSortType);
    }

    private static boolean isFirstIdx(Long previousItemId) {
        return previousItemId < 0;
    }
}

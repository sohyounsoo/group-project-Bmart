package com.group.Bmart.domain.item.support;

import com.group.Bmart.domain.category.MainCategory;
import com.group.Bmart.domain.category.SubCategory;
import com.group.Bmart.domain.item.Item;
import com.group.Bmart.domain.item.ItemSortType;
import com.group.Bmart.domain.item.LikeItem;
import com.group.Bmart.domain.item.controller.request.RegisterItemRequest;
import com.group.Bmart.domain.item.controller.request.RegisterLikeItemRequest;
import com.group.Bmart.domain.item.controller.request.UpdateItemRequest;
import com.group.Bmart.domain.item.service.request.*;
import com.group.Bmart.domain.item.service.response.FindItemsResponse;
import com.group.Bmart.domain.item.service.response.FindLikeItemsResponse;
import com.group.Bmart.domain.item.service.response.FindNewItemsResponse;
import com.group.Bmart.domain.user.User;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static com.group.Bmart.domain.category.fixture.CategoryFixture.mainCategory;
import static com.group.Bmart.domain.category.fixture.CategoryFixture.subCategory;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ItemFixture {

    private static final Long ITEM_ID = 1L;
    private static final String NAME = "아이템이름";
    private static final int PRICE = 20000;
    private static final String DESCRIPTION = "아이템설명";
    private static final int QUANTITY = 10;
    private static final int DISCOUNT = 0;
    private static final int REVIEW_COUNT = 0;
    private static final int LIKE_COUNT = 0;
    private static final int RATE = 0;
    private static final int MAX_QUANTITY = 10;
    private static final Long USER_ID = 1L;
    private static final Long LIKE_ITEM_ID = 1L;
    private static final String ITEM_SORT_TYPE = ItemSortType.NEW.name();
    private static final MainCategory MAIN_CATEGORY = mainCategory();
    private static final SubCategory SUB_CATEGORY = subCategory(MAIN_CATEGORY);

    public static Item item() {
        return new Item(NAME, PRICE, DESCRIPTION, QUANTITY, DISCOUNT, MAX_QUANTITY, MAIN_CATEGORY,
                SUB_CATEGORY);
    }

    public static Item item(int quantity) {
        return new Item(NAME, PRICE, DESCRIPTION, QUANTITY, DISCOUNT, quantity, MAIN_CATEGORY,
                SUB_CATEGORY);
    }

    public static Item item(MainCategory mainCategory, SubCategory subCategory) {
        return new Item(NAME, PRICE, DESCRIPTION, QUANTITY, DISCOUNT, MAX_QUANTITY, mainCategory,
                subCategory);
    }

    public static LikeItem likeItem(User user, Item item) {
        return new LikeItem(user, item);
    }

    public static FindItemsResponse findItemsResponse() {
        return new FindItemsResponse(List.of(findItemResponse()));
    }

    public static FindItemsResponse.FindItemResponse findItemResponse() {
        return new FindItemsResponse.FindItemResponse(1L, NAME, PRICE, DISCOUNT, REVIEW_COUNT,
                LIKE_COUNT,
                RATE);
    }

    public static FindItemsByCategoryCommand findItemsByCategoryCommand(
            String mainCategoryName, String subCategoryName) {
        return FindItemsByCategoryCommand.of(-1L, -1L, mainCategoryName, subCategoryName, 3,
                ITEM_SORT_TYPE);
    }


    public static RegisterLikeItemRequest registerLikeItemRequest() {
        return new RegisterLikeItemRequest(ITEM_ID);
    }

    public static DeleteLikeItemCommand deleteLikeItemCommand() {
        return new DeleteLikeItemCommand(USER_ID, LIKE_ITEM_ID);
    }

    public static FindLikeItemsCommand findLikeItemsCommand() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        return new FindLikeItemsCommand(USER_ID, pageRequest);
    }

    public static FindLikeItemsResponse findLikeItemsResponse() {
        FindLikeItemsResponse.FindLikeItemResponse findLikeItemResponse = new FindLikeItemsResponse.FindLikeItemResponse(
                LIKE_ITEM_ID,
                ITEM_ID,
                NAME,
                PRICE,
                DISCOUNT,
                REVIEW_COUNT,
                LIKE_COUNT,
                RATE
        );
        return new FindLikeItemsResponse(List.of(findLikeItemResponse), 0, 1);
    }

    public static FindNewItemsCommand findNewItemsCommand() {
        PageRequest pageRequest = PageRequest.of(0, 5);
        return new FindNewItemsCommand(null, null, pageRequest, ItemSortType.NEW);
    }

    public static UpdateItemRequest updateItemRequest() {
        return new UpdateItemRequest(NAME, PRICE, QUANTITY, DISCOUNT, MAX_QUANTITY, DESCRIPTION,
                1L, 1L);
    }

    public static UpdateItemCommand updateItemCommand() {
        return UpdateItemCommand.of(ITEM_ID, updateItemRequest());
    }

    public static RegisterItemRequest registerItemRequest() {
        return RegisterItemRequest.builder()
                .name(NAME)
                .price(PRICE)
                .description(DESCRIPTION)
                .quantity(QUANTITY)
                .discount(DISCOUNT)
                .maxBuyQuantity(MAX_QUANTITY)
                .mainCategoryId(1L)
                .subCategoryId(1L)
                .build();

    }

    public static RegisterItemCommand registerItemCommand() {
        RegisterItemRequest registerItemRequest = registerItemRequest();
        return RegisterItemCommand.of(
                registerItemRequest.name(),
                registerItemRequest.price(),
                registerItemRequest.description(),
                registerItemRequest.quantity(),
                registerItemRequest.discount(),
                registerItemRequest.maxBuyQuantity(),
                registerItemRequest.mainCategoryId(),
                registerItemRequest.subCategoryId()
        );
    }

    public static FindNewItemsResponse findNewItemsResponse() {
        return new FindNewItemsResponse(List.of(
                new FindNewItemsResponse.FindNewItemResponse(1L, NAME, PRICE, DISCOUNT, 0L, RATE)));
    }
}

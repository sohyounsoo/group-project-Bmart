package com.group.Bmart.domain.item.controller.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;
import org.hibernate.validator.constraints.Range;

@Builder
public record RegisterItemRequest(

    @NotBlank(message = "상품명은 필수 항목입니다.")
    String name,

    @PositiveOrZero(message = "상품 가격은 0원 이상이어야 합니다.")
    @NotNull(message = "상품 가격은 필수 항목입니다.")
    Integer price,

    @PositiveOrZero(message = "상품 수량은 0개 이상이어야 합니다.")
    @NotNull(message = "상품 수량은 필수 항목입니다.")
    Integer quantity,

    @Range(min = 0, max = 100, message = "상품 할인율은 0~100% 사이만 가능합니다.")
    int discount,

    @NotNull(message = "최대 주문 수량은 필수 항목입니다.")
    Integer maxBuyQuantity,

    String description,
    Long mainCategoryId,
    Long subCategoryId
) {

}

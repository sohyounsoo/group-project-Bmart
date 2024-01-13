package com.group.Bmart.domain.category.service.request;

import com.group.Bmart.domain.category.controller.request.RegisterMainCategoryRequest;
import jakarta.validation.constraints.NotBlank;

public record RegisterMainCategoryCommand(
    @NotBlank(message = "대카테고리명은 필수 항목입니다.") String name
) {

    public static RegisterMainCategoryCommand from(
        RegisterMainCategoryRequest registerMainCategoryRequest) {
        return new RegisterMainCategoryCommand(registerMainCategoryRequest.name());
    }
}

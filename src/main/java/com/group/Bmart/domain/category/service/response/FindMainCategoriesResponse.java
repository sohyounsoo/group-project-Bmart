package com.group.Bmart.domain.category.service.response;

import com.group.Bmart.domain.category.MainCategory;

import java.util.List;
import java.util.stream.Collectors;

public record FindMainCategoriesResponse(List<String> mainCategoryNames) {

    public static FindMainCategoriesResponse from(final List<MainCategory> mainCategories) {
        List<String> mainCategoryNames = mainCategories.stream()
            .map(MainCategory::getName)
            .collect(Collectors.toList());
        return new FindMainCategoriesResponse(mainCategoryNames);
    }
}

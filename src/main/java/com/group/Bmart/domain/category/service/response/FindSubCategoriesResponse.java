package com.group.Bmart.domain.category.service.response;

import com.group.Bmart.domain.category.SubCategory;

import java.util.List;
import java.util.stream.Collectors;

public record FindSubCategoriesResponse(List<String> subCategoryNames) {

    public static FindSubCategoriesResponse from(final List<SubCategory> subCategories) {
        List<String> subCategoryNames = subCategories.stream()
            .map(SubCategory::getName)
            .collect(Collectors.toList());
        return new FindSubCategoriesResponse(subCategoryNames);
    }
}

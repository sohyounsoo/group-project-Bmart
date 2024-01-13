package com.group.Bmart.domain.category.controller;

import com.group.Bmart.domain.category.controller.request.RegisterMainCategoryRequest;
import com.group.Bmart.domain.category.controller.request.RegisterSubCategoryRequest;
import com.group.Bmart.domain.category.exception.DuplicateCategoryNameException;
import com.group.Bmart.domain.category.service.CategoryService;
import com.group.Bmart.domain.category.service.request.RegisterMainCategoryCommand;
import com.group.Bmart.domain.category.service.request.RegisterSubCategoryCommand;
import com.group.Bmart.domain.category.service.response.FindMainCategoriesResponse;
import com.group.Bmart.domain.category.service.response.FindSubCategoriesResponse;
import com.group.Bmart.global.util.ErrorTemplate;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class CategoryController {

    private final CategoryService categoryService;
    private static final String MAIN_CATEGORY_BASE_URL = "/api/v1/main-categories/";
    private static final String SUB_CATEGORY_BASE_URL = "/api/v1/sub-categories/";

    @PostMapping("/main-categories")
    public ResponseEntity<Void> saveMainCategory(
        @RequestBody @Valid RegisterMainCategoryRequest registerMainCategoryRequest) {

        RegisterMainCategoryCommand registerMainCategoryCommand = RegisterMainCategoryCommand.from(
            registerMainCategoryRequest);
        Long savedMainCategoryId = categoryService.saveMainCategory(registerMainCategoryCommand);
        URI location = URI.create(MAIN_CATEGORY_BASE_URL + savedMainCategoryId);
        return ResponseEntity.created(location).build();
    }

    @PostMapping("/sub-categories")
    public ResponseEntity<Void> saveSubCategory(
        @RequestBody @Valid RegisterSubCategoryRequest registerSubCategoryRequest) {

        RegisterSubCategoryCommand registerSubCategoryCommand = RegisterSubCategoryCommand.from(
            registerSubCategoryRequest);
        Long savedSubCategoryId = categoryService.saveSubCategory(registerSubCategoryCommand);
        URI location = URI.create(SUB_CATEGORY_BASE_URL + savedSubCategoryId);
        return ResponseEntity.created(location).build();
    }

    @GetMapping("/categories")
    public ResponseEntity<FindMainCategoriesResponse> findAllMainCategories() {
        FindMainCategoriesResponse findMainCategoriesResponse = categoryService.findAllMainCategories();
        return ResponseEntity.ok(findMainCategoriesResponse);
    }

    @GetMapping("/categories/{mainCategoryId}")
    public ResponseEntity<FindSubCategoriesResponse> findSubCategories(
        @PathVariable Long mainCategoryId) {

        FindSubCategoriesResponse findSubCategoriesResponse = categoryService.findSubCategoriesByMainCategory(
            mainCategoryId);
        return ResponseEntity.ok(findSubCategoriesResponse);
    }

    @ExceptionHandler(DuplicateCategoryNameException.class)
    public ResponseEntity<ErrorTemplate> handleException(
        final DuplicateCategoryNameException duplicateCategoryNameException) {
        return ResponseEntity.badRequest()
            .body(ErrorTemplate.of(duplicateCategoryNameException.getMessage()));
    }
}

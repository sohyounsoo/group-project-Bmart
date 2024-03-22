package com.group.Bmart.domain.category.service;

import com.group.Bmart.domain.category.MainCategory;
import com.group.Bmart.domain.category.SubCategory;
import com.group.Bmart.domain.category.exception.DuplicateCategoryNameException;
import com.group.Bmart.domain.category.fixture.CategoryFixture;
import com.group.Bmart.domain.category.repository.MainCategoryRepository;
import com.group.Bmart.domain.category.repository.SubCategoryRepository;
import com.group.Bmart.domain.category.service.request.RegisterMainCategoryCommand;
import com.group.Bmart.domain.category.service.request.RegisterSubCategoryCommand;
import com.group.Bmart.domain.category.service.response.FindMainCategoriesResponse;
import com.group.Bmart.domain.category.service.response.FindSubCategoriesResponse;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;


@Slf4j
@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @InjectMocks
    private CategoryService categoryService;

    @Mock
    private MainCategoryRepository mainCategoryRepository;

    @Mock
    private SubCategoryRepository subCategoryRepository;

    @Nested
    @DisplayName("saveMainCatory 메서드 실행 시")
    class saveMainCategoryTests {
        RegisterMainCategoryCommand registerMainCategoryCommand
                = CategoryFixture.registerMainCategoryCommand();
        MainCategory mainCategory = CategoryFixture.mainCategory();

        @Test
        @DisplayName("성공")
        public void success() {
            log.info("registerMainCategoryCommand= {}", registerMainCategoryCommand);

            // Given
            given(mainCategoryRepository.save(any(MainCategory.class))).willReturn(mainCategory);

            // When
            categoryService.saveMainCategory(registerMainCategoryCommand);

            // Then
            verify(mainCategoryRepository, times(1)).save(any(MainCategory.class));
        }

        @Test
        @DisplayName("에외: 이미 존재하는 MainCategory 이름")
        public void throwExceptionWhenNameIsDuplicated() {
            //given
            given(mainCategoryRepository.existsByName(registerMainCategoryCommand.name())).willReturn(true);

            //when & then
            assertThatThrownBy(() -> categoryService.saveMainCategory(registerMainCategoryCommand))
                    .isInstanceOf(DuplicateCategoryNameException.class);

        }
    }

    @Nested
    @DisplayName("saveSubCategory 메서드 실행 시")
    class SaveSubCategoryTests {
        RegisterSubCategoryCommand registerSubCategoryCommand = CategoryFixture.registerSubCategoryCommand();
        MainCategory mainCategory = CategoryFixture.mainCategory();
        SubCategory subCategory = CategoryFixture.subCategory(mainCategory);

        @Test
        @DisplayName("성공")
        public void success() {
            // Given
            given(mainCategoryRepository.findById(anyLong())).willReturn(Optional.of(mainCategory));
            given(subCategoryRepository.existsByMainCategoryAndName(any(), any())).willReturn(false);
            given(subCategoryRepository.save(any())).willReturn(subCategory);

            //when
            categoryService.saveSubCategory(registerSubCategoryCommand);

            //then
            verify(subCategoryRepository, times(1)).save(any(SubCategory.class));
            verify(mainCategoryRepository, times(1)).findById(anyLong());
        }

        @Test
        @DisplayName("예외: MainCategory 내 이미 존재하는 SubCategory 이름")
        public void throwExceptionWhenNameIsDuplicated() {
            //given
            given(mainCategoryRepository.findById(anyLong())).willReturn(Optional.of(mainCategory));
            given(subCategoryRepository.existsByMainCategoryAndName(any(), any())).willReturn(true);

            //when & then
            assertThatThrownBy(() -> categoryService.saveSubCategory(registerSubCategoryCommand))
                    .isInstanceOf(DuplicateCategoryNameException.class);
        }
    }

    @Nested
    @DisplayName("모든 대카테고리 조회 메서드 호출 시")
    class FindAllMainCategories {

        List<String> mainCategoryNames = getMainCategoryNames();
        List<MainCategory> mainCategories = getMainCategories();

        @Test
        @DisplayName("성공")
        public void success() {

            //given
            given(mainCategoryRepository.findAll()).willReturn(mainCategories);

            //when
            FindMainCategoriesResponse findMainCategoriesResponse = categoryService.findAllMainCategories();

            //then
            assertThat(findMainCategoriesResponse.mainCategoryNames())
                    .usingRecursiveComparison()
                    .isEqualTo(mainCategoryNames);
        }
    }

    @Nested
    @DisplayName("대카테고리의 소카테고리 조회 메서드 호출 시")
    class FindSubCategoriesByMainCategory {

        List<String> subCategoryNames = getSubCategoryNames();
        List<SubCategory> subCategories = getSubCategories();
        MainCategory mainCategory = CategoryFixture.mainCategory();

        @Test
        @DisplayName("성공")
        public void success() {

            //given
            given(subCategoryRepository.findByMainCategory(mainCategory)).willReturn(subCategories);
            given(mainCategoryRepository.findById(anyLong())).willReturn(Optional.ofNullable(mainCategory));

            //when
            FindSubCategoriesResponse findSubCategoriesResponse
                    = categoryService.findSubCategoriesByMainCategory(anyLong());

            log.info("findSubCategoriesResponse= {}", findSubCategoriesResponse.subCategoryNames());

            //then
            assertThat(findSubCategoriesResponse.subCategoryNames())
                    .usingRecursiveComparison()
                    .isEqualTo(subCategoryNames);
        }
    }

    private List<String> getMainCategoryNames() {
        return List.of("main1", "main2", "main3");
    }

    private List<MainCategory> getMainCategories() {
        MainCategory mainCategory1 = new MainCategory("main1");
        MainCategory mainCategory2 = new MainCategory("main2");
        MainCategory mainCategory3 = new MainCategory("main3");

        return List.of(mainCategory1, mainCategory2, mainCategory3);
    }

    private List<SubCategory> getSubCategories() {
        MainCategory mainCategory = new MainCategory("main");
        SubCategory subCategory1 = new SubCategory(mainCategory, "sub1");
        SubCategory subCategory2 = new SubCategory(mainCategory, "sub2");
        SubCategory subCategory3 = new SubCategory(mainCategory, "sub3");
        return List.of(subCategory1, subCategory2, subCategory3);
    }

    private List<String> getSubCategoryNames() {
        return List.of("sub1", "sub2", "sub3");
    }


}
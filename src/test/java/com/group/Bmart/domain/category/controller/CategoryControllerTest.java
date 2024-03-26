package com.group.Bmart.domain.category.controller;

import com.group.Bmart.base.BaseControllerTest;
import com.group.Bmart.domain.category.controller.request.RegisterMainCategoryRequest;
import com.group.Bmart.domain.category.controller.request.RegisterSubCategoryRequest;
import com.group.Bmart.domain.category.fixture.CategoryFixture;
import com.group.Bmart.domain.category.service.response.FindMainCategoriesResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CategoryControllerTest extends BaseControllerTest {

    @Nested
    @DisplayName("대카테고리 저장하는 api 호출 시")
    class SaveMainCategoryApi {

        RegisterMainCategoryRequest registerMainCategoryRequest = CategoryFixture.registerMainCategoryRequest();

        @Test
        @DisplayName("성공")
        public void saveMainCategory() throws Exception {
            // given
            when(categoryService.saveMainCategory(any())).thenReturn(1L);

            // when
            mockMvc.perform(post("/api/v1/main-categories")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(registerMainCategoryRequest)))
                    .andExpect(status().isCreated())
                    .andExpect(header().string("Location", "/api/v1/main-categories/1"))
                    .andDo(print())
                    .andDo(restDocs.document(requestFields(
                                    fieldWithPath("name").type(STRING)
                                            .description("대카테고리명")
                            )
                    ));

            //then
            verify(categoryService, times(1)).saveMainCategory(any());
        }
    }

    @Nested
    @DisplayName("소카테고리 저장하는 api 호출 시")
    class SaveSubCategoryApi {


        RegisterSubCategoryRequest registerSubCategoryRequest = CategoryFixture.registerSubCategoryRequest();

        @Test
        @DisplayName("성공")
        public void saveSubCategory() throws Exception {
            // given
            when(categoryService.saveSubCategory(any())).thenReturn(1L);

            // when
            mockMvc.perform(post("/api/v1/sub-categories")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(registerSubCategoryRequest)))
                    .andExpect(status().isCreated())
                    .andExpect(header().string("Location", "/api/v1/sub-categories/1"))
                    .andDo(print())
                    .andDo(restDocs.document(requestFields(
                                    fieldWithPath("mainCategoryId").type(NUMBER)
                                            .description("대카테고리 Id"),
                                    fieldWithPath("name").type(STRING)
                                            .description("소카테고리명")
                            )
                    ));

            // then
            verify(categoryService, times(1)).saveSubCategory(any());
        }
    }

    @Nested
    @DisplayName("모든 카테고리 조회 api 호출 시")
    class findAllMainCategoriesApi {

        @Test
        @DisplayName("성공")
        public void findMainCategoriesApi() throws Exception {
            //given
            FindMainCategoriesResponse mainCategoriesResponse
                    = CategoryFixture.findMainCategoriesResponse();
            given(categoryService.findAllMainCategories()).willReturn(mainCategoriesResponse);

            //when
            ResultActions resultActions = mockMvc.perform(get("/api/v1/categories")
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(restDocs.document(responseFields(
                        fieldWithPath("mainCategoryNames").type(ARRAY)
                                .description("대카테고리 리스트")
                    )
                ));

            // then
            resultActions.andExpect(status().isOk())
                    .andDo(restDocs.document(
                            responseFields(
                                    fieldWithPath("mainCategoryNames").type(ARRAY).description("대카테고리")
                            )
                    ));
        }

    }

    @Nested
    @DisplayName("소카테고리 조회 api 호출 시")
    class findSubCategoriesApi {

        @Test
        @DisplayName("성공")
        public void findSubCategories() throws Exception {
            //given
            FindMainCategoriesResponse mainCategoriesResponse
                    = CategoryFixture.findMainCategoriesResponse();
            given(categoryService.findAllMainCategories()).willReturn(mainCategoriesResponse);

            //when
            ResultActions resultActions = mockMvc.perform(get("/api/v1/categories")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andDo(restDocs.document(responseFields(
                                    fieldWithPath("mainCategoryNames").type(ARRAY)
                                            .description("대카테고리 리스트")
                            )
                    ));

            // then
            resultActions.andExpect(status().isOk())
                    .andDo(restDocs.document(
                            responseFields(
                                    fieldWithPath("mainCategoryNames").type(ARRAY).description("대카테고리")
                            )
                    ));
        }

    }

}
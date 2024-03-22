package com.group.Bmart.domain.category.controller;

import com.group.Bmart.base.BaseControllerTest;
import com.group.Bmart.domain.category.controller.request.RegisterMainCategoryRequest;
import com.group.Bmart.domain.category.fixture.CategoryFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
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

}
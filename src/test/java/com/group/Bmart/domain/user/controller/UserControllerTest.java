package com.group.Bmart.domain.user.controller;

import com.group.Bmart.base.BaseControllerTest;
import com.group.Bmart.domain.user.User;
import com.group.Bmart.domain.user.service.response.FindUserDetailResponse;
import com.group.Bmart.domain.user.support.UserFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserControllerTest extends BaseControllerTest {
    @Nested
    @DisplayName("findUser 메서드 실행 시")
    class FindUserTest {

        @Test
        @DisplayName("성공")
        void FindUser() throws Exception {
            //given
            FindUserDetailResponse findUserDetailResponse = UserFixture.findUserDetailResponse();

            given(userService.findUser(any())).willReturn(findUserDetailResponse);

            //when
            ResultActions resultActions = mockMvc.perform(get("/api/v1/users/me")
                    .header("Authorization", accessToken)
                    .accept(MediaType.APPLICATION_JSON));

            //then
            resultActions.andExpect(status().isOk())
                    .andDo(restDocs.document(
                            requestHeaders(headerWithName("Authorization").description("액세스 토큰")),
                            responseFields(
                                    fieldWithPath("id").type(JsonFieldType.STRING).description("응답 ID"),
                                    fieldWithPath("dateTime").type(JsonFieldType.STRING).description("응답 날짜 및 시간"),
                                    fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                                    subsectionWithPath("response").description("사용자 정보"),
                                    fieldWithPath("response.userId").type(JsonFieldType.NUMBER).description("유저 ID"),
                                    fieldWithPath("response.nickname").type(JsonFieldType.STRING).description("유저 닉네임"),
                                    fieldWithPath("response.email").type(JsonFieldType.STRING).description("유저 이메일"),
                                    fieldWithPath("response.provider").type(JsonFieldType.STRING).description("유저 리소스 서버"),
                                    fieldWithPath("response.providerId").type(JsonFieldType.STRING).description("유저 리소스 서버 ID"),
                                    fieldWithPath("response.userRole").type(JsonFieldType.STRING).description("유저 권한"),
                                    fieldWithPath("response.userGrade").type(JsonFieldType.STRING).description("유저 등급")
                            )
                    ));
        }
    }

        @Nested
        @DisplayName("deleteUser 메서드 실행 시")
        class DeleteUserTest {

            @Test
            @DisplayName("성공")
            void DeleteUser() throws Exception {
                //given
                User user = UserFixture.user();
                FindUserDetailResponse findUserDetailResponse = FindUserDetailResponse.from(user);

                given(userService.findUser(any())).willReturn(findUserDetailResponse);

                //when
                ResultActions resultActions = mockMvc.perform(delete("/api/v1/users/me")
                        .header("Authorization", accessToken));

                //then
                resultActions.andExpect(status().isOk())
                        .andDo(restDocs.document(
                            requestHeaders(
                                    headerWithName("Authorization").description("액세스 토큰")
                            )
                        ));
            }
        }
}
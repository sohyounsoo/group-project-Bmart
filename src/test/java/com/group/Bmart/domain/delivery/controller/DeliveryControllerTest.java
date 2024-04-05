package com.group.Bmart.domain.delivery.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.group.Bmart.base.BaseControllerTest;
import com.group.Bmart.domain.delivery.controller.request.RegisterDeliveryRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

class DeliveryControllerTest extends BaseControllerTest {

    @Test
    @DisplayName("배달 생성 api 호출 시")
    void registerDelivery() throws Exception {
        //given
        RegisterDeliveryRequest registerDeliveryRequest
                = new RegisterDeliveryRequest(60);

        given(deliveryService.registerDelivery(any())).willReturn(1L);

        //when
        ResultActions resultActions = mockMvc.perform(
                post("/api/v1/orders/{orderId}/deliveries", 1L)
                        .header(AUTHORIZATION, accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(registerDeliveryRequest))
        );

        //then
        resultActions.andExpect(status().isCreated())
            .andDo(restDocs.document(
                requestHeaders(
                        headerWithName(AUTHORIZATION).description("액세스 토큰")
                ),
                pathParameters(
                        parameterWithName("orderId").description("주문 ID")
                ),
                requestFields(
                        fieldWithPath("estimateMinutes").type(NUMBER).description("배달 예상 시간(분)")
                ),
                responseHeaders(
                        headerWithName("Location").description("생성된 리소스 위치")
                )
            ));

    }

}
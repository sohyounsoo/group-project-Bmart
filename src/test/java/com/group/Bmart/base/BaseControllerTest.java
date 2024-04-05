package com.group.Bmart.base;

import com.group.Bmart.domain.cart.service.CartItemService;
import com.group.Bmart.domain.category.service.CategoryService;
import com.group.Bmart.domain.coupon.service.CouponService;
import com.group.Bmart.domain.delivery.service.DeliveryService;
import com.group.Bmart.domain.item.service.ItemService;
import com.group.Bmart.domain.item.service.LikeItemService;
import com.group.Bmart.domain.order.service.OrderService;
import com.group.Bmart.domain.payment.service.PaymentClient;
import com.group.Bmart.domain.payment.service.PaymentService;
import com.group.Bmart.domain.review.service.ReviewService;
import com.group.Bmart.domain.user.service.UserService;
import com.group.Bmart.global.auth.jwt.JwtAuthenticationProvider;
import com.group.Bmart.global.auth.jwt.filter.JwtAuthenticationFilter;
import com.group.Bmart.global.auth.oauth.client.OAuthRestClient;
import com.group.Bmart.global.infrastructure.ApiService;
import com.group.Bmart.domain.user.support.AuthFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@WebMvcTest
@Import(RestDocsConfig.class)
@ExtendWith(RestDocumentationExtension.class)
public abstract class BaseControllerTest {

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected RestDocumentationResultHandler restDocs;

    @MockBean
    protected UserService userService;

    @MockBean
    protected CartItemService cartItemService;

    @MockBean
    protected CategoryService categoryService;

    @MockBean
    protected CouponService couponService;

//    @MockBean
//    protected EventService eventService;

//    @MockBean
//    protected EventItemService eventItemService;

    @MockBean
    protected OAuthRestClient oAuthRestClient;

    @MockBean
    protected PaymentService paymentService;

    @MockBean
    protected ApiService apiService;

    @MockBean
    protected PaymentClient paymentClient;

    @MockBean
    protected ReviewService reviewService;

    @MockBean
    protected ItemService itemService;

    @MockBean
    protected LikeItemService likeItemService;

    @MockBean
    protected DeliveryService deliveryService;

    @MockBean
    protected OrderService orderService;

//    @MockBean
//    protected RiderAuthenticationService riderAuthenticationService;

//    @MockBean
//    protected NotificationService notificationService;

    protected static final String AUTHORIZATION = "Authorization";


    protected String accessToken;

    @BeforeEach
    void authenticationSetUp() {
        accessToken = AuthFixture.accessToken();
    }

    @BeforeEach
    void mockMvcSetUp(
            final WebApplicationContext context,
            final RestDocumentationContextProvider provider) {
        JwtAuthenticationProvider jwtAuthenticationProvider
                = new JwtAuthenticationProvider(AuthFixture.tokenProvider());

        // MockMvc를 설정합니다.
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .alwaysDo(print()) // 모든 요청과 응답을 콘솔에 출력합니다.
                .alwaysDo(restDocs) // Spring REST Docs 설정을 적용합니다.
                .alwaysDo(
                        document("{method-name}",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()))
                )
                .addFilter(new CharacterEncodingFilter("UTF-8", true))
                .addFilter(new JwtAuthenticationFilter(jwtAuthenticationProvider))
                .apply(MockMvcRestDocumentation.documentationConfiguration(provider))
                .build();
    }

    @Test
    void contextLoads() {

    }
}
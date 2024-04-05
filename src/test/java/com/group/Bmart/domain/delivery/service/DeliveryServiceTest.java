package com.group.Bmart.domain.delivery.service;

import com.group.Bmart.domain.delivery.exception.AlreadyRegisteredDeliveryException;
import com.group.Bmart.domain.delivery.exception.UnauthorizedDeliveryException;
import com.group.Bmart.domain.delivery.repository.DeliveryRepository;
import com.group.Bmart.domain.delivery.service.request.RegisterDeliveryCommand;
import com.group.Bmart.domain.order.Order;
import com.group.Bmart.domain.order.exception.NotFoundOrderException;
import com.group.Bmart.domain.order.repository.OrderRepository;
import com.group.Bmart.domain.order.support.OrderFixture;
import com.group.Bmart.domain.user.User;
import com.group.Bmart.domain.user.repository.UserRepository;
import com.group.Bmart.domain.user.support.UserFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class DeliveryServiceTest {

    @InjectMocks
    DeliveryService deliveryService;

    @Mock
    DeliveryRepository deliveryRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    OrderRepository orderRepository;

    @Nested
    @DisplayName("registerDelivery 메서드 실행 시")
    class RegisterDeliverTest {

        User user = UserFixture.user();
        User employee = UserFixture.employee();
        Order order = OrderFixture.payedOrder(1L, user);

        RegisterDeliveryCommand registerDeliveryCommand = RegisterDeliveryCommand.of(
                1L,
                1L,
                60);

        @Test
        @DisplayName("성공")
        void success() {
            //given
            given(userRepository.findById(any())).willReturn(Optional.ofNullable(employee));
            given(orderRepository.findByIdPessimistic(any())).willReturn(Optional.ofNullable(order));
            given(deliveryRepository.existsByOrder(any())).willReturn(false);

            //when
            deliveryService.registerDelivery(registerDeliveryCommand);

            //then
            then(deliveryRepository).should().save(any());
        }

        @Test
        @DisplayName("예외 로그인 유저가 employee가 아님")
        void throwExceptionWhenLoginUserIsNotEmployee() {
            //given
            given(userRepository.findById(any())).willReturn(Optional.ofNullable(user));

            //when
            //then
            assertThatThrownBy(() -> deliveryService.registerDelivery(registerDeliveryCommand))
                    .isInstanceOf(UnauthorizedDeliveryException.class);
        }

        @Test
        @DisplayName("예외: 존재하지 않는 order")
        void throwExceptionWhenNotFoundOrder() {
            //given
            given(userRepository.findById(any())).willReturn(Optional.ofNullable(user));
            given(orderRepository.findByIdPessimistic(any())).willReturn(Optional.empty());

            //when
            //then
            assertThatThrownBy(() -> deliveryService.registerDelivery(registerDeliveryCommand))
                    .isInstanceOf(NotFoundOrderException.class);
        }

        @Test
        @DisplayName("예외: 이미 배달이 만들어진 주문")
        void throwExceptionWhenAlreadyRegisteredDelivery() {
            //given
            given(userRepository.findById(any())).willReturn(Optional.ofNullable(employee));
            given(orderRepository.findByIdPessimistic(any())).willReturn(Optional.ofNullable(order));
            given(deliveryRepository.existsByOrder(any())).willReturn(true); //중복

            //when
            //then
            assertThatThrownBy(() -> deliveryService.registerDelivery(registerDeliveryCommand))
                    .isInstanceOf(AlreadyRegisteredDeliveryException.class);
        }

    }



}
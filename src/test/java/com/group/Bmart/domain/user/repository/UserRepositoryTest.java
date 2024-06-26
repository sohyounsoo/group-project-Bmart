package com.group.Bmart.domain.user.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.group.Bmart.base.TestQueryDslConfig;
import com.group.Bmart.domain.order.Order;
import com.group.Bmart.domain.order.OrderStatus;
import com.group.Bmart.domain.order.repository.OrderRepository;
import com.group.Bmart.domain.user.User;
import com.group.Bmart.domain.user.UserGrade;
import com.group.Bmart.domain.user.UserRole;
import com.group.Bmart.domain.user.repository.response.UserOrderCount;
import com.group.Bmart.global.config.JpaAuditingConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import({JpaAuditingConfig.class, TestQueryDslConfig.class})
class UserRepositoryTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    TestEntityManager em;

    private User createAndSaveUser() {
        User user = User.builder()
                .nickname("이름")
                .email("email@email.com")
                .provider("provider")
                .providerId("providerId")
                .userGrade(UserGrade.NORMAL)
                .userRole(UserRole.ROLE_USER)
                .build();
        userRepository.save(user);
        return user;
    }

    private List<Order> createAndSaveAllOrders(User user, int end) {
        List<Order> orders = new ArrayList<>();
        for (int i=0; i<end; i++) {
            Order order = createOrder(user);
            orders.add(order);
        }
        orderRepository.saveAll(orders);

        return orders;
    }

    private Order createOrder(User user) {
        return Order.builder()
                .name("name")
                .uuid(UUID.randomUUID().toString())
                .userCoupon(null)
                .price(1000)
                .user(user)
                .status(OrderStatus.COMPLETED)
                .build();
    }

    @Nested
    @DisplayName("getUserORderCount 메서드 실행 시")
    class GetUserOrderCountTest {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = now.minusDays(1);
        LocalDateTime end = now.plusDays(1);

        @Test
        @DisplayName("성공: 유저 1, 주문 5")
        void successWhenOneUserWithFiveOrder() {
            //given
            User user = createAndSaveUser();
            List<Order> orders = createAndSaveAllOrders(user, 5);

            //when
            List<UserOrderCount> userOrderCounts
                    = userRepository.getUserOrderCount(start, end);

            //then
            assertThat(userOrderCounts).hasSize(1);
            assertThat(userOrderCounts.get(0).getOrderCount()).isEqualTo(5);
        }

        @Test
        @DisplayName("성공: 유저 2, 주문 10, 5")
        void successWhenTwoUserWithTenAndFiveOrder() {
            //given
            User user1 = createAndSaveUser();
            List<Order> orders1 = createAndSaveAllOrders(user1, 5);

            User user2 = createAndSaveUser();
            List<Order> orders2 = createAndSaveAllOrders(user2, 5);

            //when
            List<UserOrderCount> userOrderCounts
                    = userRepository.getUserOrderCount(start, end);

            //then
            assertThat(userOrderCounts).hasSize(2);
            assertThat(userOrderCounts).map(UserOrderCount::getUserId)
                    .containsExactlyInAnyOrder(user1.getUserId(), user2.getUserId());
            assertThat(userOrderCounts).map(UserOrderCount::getOrderCount)
                    .containsExactlyInAnyOrder(10, 5);
        }
    }

    @Nested
    @DisplayName("updateUserGrade 메서드 실행 시")
    class UpdateUserGradeTest {

        @Test
        @DisplayName("성공")
        void success() {
            //given
            List<User> users = IntStream.range(0, 5)
                    .mapToObj(i -> createAndSaveUser())
                    .toList();

            List<Long> userIds = users.stream()
                    .map(User::getUserId)
                    .toList();
            em.flush();
            em.clear();

            //when
            userRepository.updateUserGrade(UserGrade.VIP, userIds);

            //then
            List<User> findUsers = userRepository.findAll();
            assertThat(findUsers).map(User::getUserGrade).containsOnly(UserGrade.VIP);
        }
    }





}
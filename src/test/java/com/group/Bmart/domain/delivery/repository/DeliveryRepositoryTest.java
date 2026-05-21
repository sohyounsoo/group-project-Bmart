package com.group.Bmart.domain.delivery.repository;

import com.group.Bmart.base.TestQueryDslConfig;
import com.group.Bmart.domain.delivery.Delivery;
import com.group.Bmart.domain.delivery.Rider;
import com.group.Bmart.domain.delivery.support.DeliveryFixture;
import com.group.Bmart.domain.order.Order;
import com.group.Bmart.domain.order.OrderStatus;
import com.group.Bmart.domain.order.repository.OrderRepository;
import com.group.Bmart.domain.user.User;
import com.group.Bmart.domain.user.repository.UserRepository;
import com.group.Bmart.domain.user.support.UserFixture;
import org.assertj.core.data.TemporalUnitOffset;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.within;

@DataJpaTest
@Import(TestQueryDslConfig.class)
public class DeliveryRepositoryTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    DeliveryRepository deliveryRepository;

    @Autowired
    RiderRepository riderRepository;

    User user = UserFixture.user();
    Rider rider = DeliveryFixture.rider();
    TemporalUnitOffset withInOneSeconds = within(1, ChronoUnit.SECONDS);

    @BeforeEach
    void init() {
        userRepository.save(user);
        riderRepository.save(rider);
    }

    private List<Order> createAndSaveOrders(int end) {
        List<Order> orders = IntStream.range(0, end)
                .mapToObj(i -> Order.builder()
                        .uuid(UUID.randomUUID().toString())
                        .price(1000)
                        .name("비비고 왕교자 1개 외 2개")
                        .user(user)
                        .status(OrderStatus.COMPLETED)
                        .build())
                .toList();
        orderRepository.saveAll(orders);
        return orders;
    }

    private List<Delivery> createAndSaveDeliveries(List<Order> orders) {
        List<Delivery> deliveries = orders.stream()
                .map(DeliveryFixture::waitingDelivery)
                .toList();
        deliveryRepository.saveAll(deliveries);
        return deliveries;
    }
}

package com.group.Bmart.domain.order.support;

import com.group.Bmart.domain.item.Item;
import com.group.Bmart.domain.order.Order;
import com.group.Bmart.domain.order.OrderItem;
import com.group.Bmart.domain.order.OrderStatus;
import com.group.Bmart.domain.user.User;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static com.group.Bmart.domain.item.support.ItemFixture.item;

public class OrderFixture {

    private static OrderItem orderItem() {
        Item item = item();
        ReflectionTestUtils.setField(item, "itemId", 1L);
        return new OrderItem(item(), 1);
    }
    public static Order payingOrder(long orderId, User user) {
        Order order = new Order(user, List.of(orderItem()));
        ReflectionTestUtils.setField(order, "orderId", orderId);
        ReflectionTestUtils.setField(order, "status", OrderStatus.PAYING);

        return order;
    }

    public static Order payedOrder(long orderId, User user) {
        Order order = payingOrder(orderId, user);
        ReflectionTestUtils.setField(order, "orderId", orderId);
        ReflectionTestUtils.setField(order, "status", OrderStatus.PAYED);

        return order;
    }
}

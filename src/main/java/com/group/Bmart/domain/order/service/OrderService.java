package com.group.Bmart.domain.order.service;

import com.group.Bmart.domain.coupon.Coupon;
import com.group.Bmart.domain.coupon.UserCoupon;
import com.group.Bmart.domain.coupon.exception.InvalidCouponException;
import com.group.Bmart.domain.coupon.exception.NotFoundUserCouponException;
import com.group.Bmart.domain.coupon.repository.UserCouponRepository;
import com.group.Bmart.domain.item.Item;
import com.group.Bmart.domain.item.exception.InvalidItemException;
import com.group.Bmart.domain.item.exception.NotFoundItemException;
import com.group.Bmart.domain.item.repository.ItemRepository;
import com.group.Bmart.domain.order.Order;
import com.group.Bmart.domain.order.OrderItem;
import com.group.Bmart.domain.order.OrderStatus;
import com.group.Bmart.domain.order.controller.request.CreateOrderRequest.CreateOrderItemRequest;
import com.group.Bmart.domain.order.exception.NotFoundOrderException;
import com.group.Bmart.domain.order.exception.UnauthorizedOrderException;
import com.group.Bmart.domain.order.repository.OrderRepository;
import com.group.Bmart.domain.order.service.request.CreateOrdersCommand;
import com.group.Bmart.domain.order.service.request.UpdateOrderByCouponCommand;
import com.group.Bmart.domain.order.service.response.*;
import com.group.Bmart.domain.payment.service.request.FindPayedOrdersCommand;
import com.group.Bmart.domain.user.User;
import com.group.Bmart.domain.user.exception.NotFoundUserException;
import com.group.Bmart.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private static final Integer PAGE_SIZE = 10;

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final UserCouponRepository userCouponRepository;


    @Transactional
    public CreateOrderResponse createOrder(final CreateOrdersCommand createOrdersCommand) {
        User findUser = findUserByUserId(createOrdersCommand.userId());
        List<OrderItem> orderItem = createOrderItem(createOrdersCommand.createOrderRequest()
            .createOrderItemRequests());
        Order order = new Order(findUser, orderItem);
        orderRepository.save(order).getOrderId();

        return CreateOrderResponse.from(order);
    }

    @Transactional
    public UpdateOrderByCouponResponse updateOrderByCoupon(
        final UpdateOrderByCouponCommand updateOrderByCouponCommand) {
        Order findOrder = getOrderByOrderIdAndUserId(updateOrderByCouponCommand.orderId(),
            updateOrderByCouponCommand.userId());
        UserCoupon findUserCoupon = findUserCouponByIdWithCoupon(
            updateOrderByCouponCommand.couponId());

        validationCoupon(findOrder, findUserCoupon.getCoupon());
        findOrder.setUserCoupon(findUserCoupon);

        return UpdateOrderByCouponResponse.of(findOrder, findUserCoupon.getCoupon());
    }

    @Transactional
    public void updateOrderStatus() {
        //30분
        LocalDateTime expiredTime = LocalDateTime.now().minusMinutes(30);
        List<OrderStatus> statusList = List.of(OrderStatus.PENDING, OrderStatus.PAYING);

        List<Order> expiredOrders = orderRepository.findByStatusInBeforeExpiredTime(
            expiredTime, statusList);

        for (Order expirdeOrder : expiredOrders) {
            updateItemQuantity(expirdeOrder);
            expirdeOrder.updateOrderStatus(OrderStatus.CANCELED);
        }
    }

    @Transactional
    public void cancelOrder(final Order order) {
        order.updateOrderStatus(OrderStatus.CANCELED);
        order.unUseCoupon();
        order.getOrderItems().forEach(
            orderItem -> itemRepository.increaseQuantity(orderItem.getItem().getItemId(),
                orderItem.getQuantity())
        );
    }

    @Transactional
    public void deleteOrder(final Long orderId, final Long userId) {
        Order order = getOrderByOrderIdAndUserId(orderId, userId);
        orderRepository.delete(order);
    }

    private static void updateItemQuantity(Order order) {
        List<OrderItem> orderItems = order.getOrderItems();
        for (OrderItem orderItem : orderItems) {
            orderItem.getItem().increaseQuantity(orderItem.getQuantity());
        }
    }

    @Transactional(readOnly = true)
    public FindOrderDetailResponse findOrderByIdAndUserId(final Long userId, final Long orderId) {
        final Order order = getOrderByOrderIdAndUserId(orderId, userId);
        return FindOrderDetailResponse.from(order);
    }

    @Transactional(readOnly = true)
    public FindOrdersResponse findOrders(final Long userId, final Integer page) {
        final Page<Order> pagination = orderRepository.findByUser_UserId(userId,
            PageRequest.of(page, PAGE_SIZE));

        return FindOrdersResponse.of(pagination.getContent(), pagination.getTotalPages());
    }

    private List<OrderItem> createOrderItem(final List<CreateOrderItemRequest> orderItemRequests) {
        List<OrderItem> orderItems = new ArrayList<>();

        for (CreateOrderItemRequest createOrderRequest : orderItemRequests) {
            Item findItem = findItemByItemId(createOrderRequest.itemId());
            Integer quantity = createOrderRequest.quantity();
            validateItemQuantity(findItem, quantity);
            findItem.decreaseQuantity(quantity);
            // OrderItem 생성 및 초기화
            OrderItem orderItem = new OrderItem(findItem, quantity);
            orderItems.add(orderItem);
        }
        return orderItems;
    }

    private void validateItemQuantity(final Item findItem, final Integer quantity) {
        if (findItem.getQuantity() - quantity < 0) {
            throw new InvalidItemException("상품의 재고 수량이 부족합니다");
        }
    }

    private UserCoupon findUserCouponByIdWithCoupon(Long UserCouponId) {
        return userCouponRepository.findByIdWithCoupon(UserCouponId)
            .orElseThrow(() -> new NotFoundUserCouponException("존재하지 않는 쿠폰입니다"));
    }

    private void validationCoupon(Order order, Coupon coupon) {
        if (order.getPrice() < coupon.getMinOrderPrice()) {
            throw new InvalidCouponException("총 주문 금액이 쿠폰 최소 사용 금액보다 작습니다");
        }
    }

    @Transactional(readOnly = true)
    public FindPayedOrdersResponse findPayedOrders(FindPayedOrdersCommand findPayedOrdersCommand) {
        checkUserHasEmployeeAuthority(findPayedOrdersCommand.userId());
        PageRequest pageRequest = PageRequest.of(findPayedOrdersCommand.page(), PAGE_SIZE);
        Page<Order> findOrders = orderRepository.findAllStatusIsPayed(pageRequest);
        return FindPayedOrdersResponse.of(
            findOrders.getContent(),
            findOrders.getNumber(),
            findOrders.getTotalElements());
    }

    private void checkUserHasEmployeeAuthority(Long userId) {
        User user = findUserByUserId(userId);
        if(!user.isEmployee()) {
            throw new UnauthorizedOrderException("권한이 없습니다.");
        }
    }

    public Order getOrderByOrderIdAndUserId(final Long orderId, final Long userId) {
        return orderRepository.findByOrderIdAndUser_UserId(orderId, userId)
            .orElseThrow(() -> new NotFoundOrderException("order 가 존재하지 않습니다"));
    }

    public Order getOrderByUuidAndUserId(final String uuid, final Long userId) {
        return orderRepository.findByUuidAndUser_UserId(uuid, userId)
            .orElseThrow(() -> new NotFoundOrderException("order 가 존재하지 않습니다"));
    }

    private User findUserByUserId(final Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new NotFoundUserException("존재하지 않은 사용자입니다."));
    }

    private Item findItemByItemId(final Long itemId) {
        return itemRepository.findByItemId(itemId)
            .orElseThrow(() -> new NotFoundItemException("존재하지 않는 상품입니다."));
    }
}

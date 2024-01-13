package com.group.Bmart.domain.order.controller;

import com.group.Bmart.domain.order.controller.request.CreateOrderRequest;
import com.group.Bmart.domain.order.controller.request.FindPayedOrdersRequest;
import com.group.Bmart.domain.order.exception.OrderException;
import com.group.Bmart.domain.order.service.OrderService;
import com.group.Bmart.domain.order.service.request.CreateOrdersCommand;
import com.group.Bmart.domain.order.service.request.UpdateOrderByCouponCommand;
import com.group.Bmart.domain.order.service.response.*;
import com.group.Bmart.domain.payment.service.request.FindPayedOrdersCommand;
import com.group.Bmart.global.auth.LoginUser;
import com.group.Bmart.global.util.ErrorTemplate;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<CreateOrderResponse> createOrder(
        @Valid @RequestBody final CreateOrderRequest createOrderRequest,
        @LoginUser final Long userId
    ) {
        CreateOrdersCommand createOrdersCommand = CreateOrdersCommand.of(userId,
            createOrderRequest);

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(orderService.createOrder(createOrdersCommand));
    }

    @PostMapping("/{orderId}/apply-coupon")
    public ResponseEntity<UpdateOrderByCouponResponse> updateOrderByCoupon(
        @PathVariable final Long orderId,
        @LoginUser final Long userId,
        @RequestParam final Long couponId
    ) {
        UpdateOrderByCouponCommand updateOrderByCouponCommand = UpdateOrderByCouponCommand.of(
            orderId, userId, couponId);

        return ResponseEntity.ok(orderService.updateOrderByCoupon(updateOrderByCouponCommand));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<FindOrderDetailResponse> findOrderByIdAndUserId(
        @PathVariable Long orderId,
        @LoginUser Long userId
    ) {
        return ResponseEntity.ok(orderService.findOrderByIdAndUserId(orderId, userId));
    }

    @GetMapping
    public ResponseEntity<FindOrdersResponse> findOrders(
        @RequestParam(defaultValue = "0") Integer page,
        @LoginUser Long userId
    ) {
        return ResponseEntity.ok(orderService.findOrders(userId, page));
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<Void> deleteOrder(
        @PathVariable Long orderId,
        @LoginUser Long userId
    ) {
        orderService.deleteOrder(orderId, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/payed")
    public ResponseEntity<FindPayedOrdersResponse> findPayedOrders(
        @ModelAttribute @Valid FindPayedOrdersRequest findPayedOrdersRequest,
        @LoginUser Long userId) {
        FindPayedOrdersCommand command = FindPayedOrdersCommand.of(
            userId,
            findPayedOrdersRequest.page());
        FindPayedOrdersResponse orders = orderService.findPayedOrders(command);
        return ResponseEntity.ok(orders);
    }

    @ExceptionHandler(OrderException.class)
    public ResponseEntity<ErrorTemplate> handleException(
        final OrderException orderException) {
        log.error(orderException.getMessage());
        return ResponseEntity.badRequest()
            .body(ErrorTemplate.of(orderException.getMessage()));
    }
}

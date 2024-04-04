package com.group.Bmart.domain.delivery;

import com.group.Bmart.domain.delivery.exception.AlreadyAssignedDeliveryException;
import com.group.Bmart.domain.delivery.exception.InvalidDeliveryException;
import com.group.Bmart.domain.delivery.exception.UnauthorizedDeliveryException;
import com.group.Bmart.domain.order.Order;
import com.group.Bmart.domain.order.OrderStatus;
import com.group.Bmart.domain.user.User;
import com.group.Bmart.global.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Delivery extends BaseTimeEntity {

    private static final int ADDRESS_LENGTH = 500;
    private static final int ZERO = 0;
    public static final String DELETED = "삭제됨";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long deliveryId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orderId")
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "riderId")
    private Rider rider;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeliveryStatus deliveryStatus;

    @Column
    private LocalDateTime arrivedAt;

    @Version
    private Long version;

    @Column
    private String address;

    @Column
    private Integer orderPrice;

    @Column
    private String riderRequest;

    @Column
    private Integer deliveryFee;

    @Column
    private Long userId;

    @Builder
    public Delivery(final Order order, final int estimateMinutes) {
        validateOrderStatus(order);
        validateEstimateMinutes(estimateMinutes);
        this.order = order;
        this.deliveryStatus = DeliveryStatus.ACCEPTING_ORDER;
        this.address = order.getAddress();
        this.orderPrice = order.getPrice();
        this.riderRequest = order.getRiderRequest();
        this.deliveryFee = order.getDeliveryFee();
        this.arrivedAt = LocalDateTime.now().plusMinutes(estimateMinutes);
        this.userId = order.getUser().getUserId();
        order.updateOrderStatus(OrderStatus.DELIVERING);
    }

    private void validateOrderStatus(Order order) {
        if(!order.isPayed()) {
            throw new InvalidDeliveryException("결제 완료된 주문이 아닙니다.");
        }
    }

    public boolean isOwnByUser(final User user) {
        return this.order.isOwnByUser(user);
    }

    public void startDelivery(final int estimateMinutes) {
        validateEstimateMinutes(estimateMinutes);
        this.arrivedAt = LocalDateTime.now().plusMinutes(estimateMinutes);
        this.deliveryStatus = DeliveryStatus.START_DELIVERY;
    }

    private void validateEstimateMinutes(final int estimateMinutes) {
        if(estimateMinutes < ZERO) {
            throw new InvalidDeliveryException("배송 예상 소요 시간은 음수일 수 없습니다.");
        }
    }

    public void checkAuthority(final Rider rider) {
        if (!this.rider.equals(rider)) {
            throw new UnauthorizedDeliveryException("권한이 없습니다.");
        }
    }

    public void assignRider(Rider rider) {
        checkAlreadyAssignedToRider();
        this.rider = rider;
    }

    private void checkAlreadyAssignedToRider() {
        if (Objects.nonNull(this.rider)) {
            throw new AlreadyAssignedDeliveryException("이미 배차 완료된 배달입니다.");
        }
    }

    public void completeDelivery() {
        this.arrivedAt = LocalDateTime.now();
        this.deliveryStatus = DeliveryStatus.DELIVERED;
    }

    public void deleteAboutUser() {
        this.order = null;
        this.address = DELETED;
    }
}

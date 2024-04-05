package com.group.Bmart.domain.delivery.service;

import com.group.Bmart.domain.delivery.Delivery;
import com.group.Bmart.domain.delivery.exception.AlreadyRegisteredDeliveryException;
import com.group.Bmart.domain.delivery.exception.NotFoundDeliveryException;
import com.group.Bmart.domain.delivery.exception.UnauthorizedDeliveryException;
import com.group.Bmart.domain.delivery.repository.DeliveryRepository;
import com.group.Bmart.domain.delivery.repository.RiderRepository;
import com.group.Bmart.domain.delivery.service.request.FindDeliveryByOrderCommand;
import com.group.Bmart.domain.delivery.service.request.RegisterDeliveryCommand;
import com.group.Bmart.domain.delivery.service.response.FindDeliveryByOrderResponse;
import com.group.Bmart.domain.order.Order;
import com.group.Bmart.domain.order.exception.NotFoundOrderException;
import com.group.Bmart.domain.order.repository.OrderRepository;
import com.group.Bmart.domain.user.User;
import com.group.Bmart.domain.user.exception.NotFoundUserException;
import com.group.Bmart.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeliveryService {

    private final DeliveryRepository deliveryRepository;
    private final UserRepository userRepository;
    private final RiderRepository riderRepository;
    private final OrderRepository orderRepository;
//    private final NotificationService notificationService;

    @Transactional
    public Long registerDelivery(RegisterDeliveryCommand registerDeliveryCommand) {
        checkUserHasRegisterDeliveryAuthority(registerDeliveryCommand.userId());

        Order order = findOrderByOrderIdPessimistic(registerDeliveryCommand);
        checkAlreadyRegisteredDelivery(order);

        Delivery delivery = new Delivery(order, registerDeliveryCommand.estimateMinutes());
        deliveryRepository.save(delivery);

        return delivery.getDeliveryId();
    }

    @Transactional(readOnly = true)
    public FindDeliveryByOrderResponse findDeliverByOrder(
            FindDeliveryByOrderCommand findDeliveryByOrderCommand) {
        User user = findUserByUserId(findDeliveryByOrderCommand.userId());
        Delivery delivery = findDeliveryByOrderWithOrder(findDeliveryByOrderCommand.orderId());
        checkAuthority(delivery, user);
        return FindDeliveryByOrderResponse.from(delivery);
    }

    private void checkAlreadyRegisteredDelivery(final Order order) {
        if (deliveryRepository.existsByOrder(order)) {
            throw new AlreadyRegisteredDeliveryException("이미 배달이 생성된 주문입니다.");
        }
    }

    private User findUserByUserId(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundUserException("존재하지않는 유저입니다."));
    }

    private Order findOrderByOrderIdPessimistic(RegisterDeliveryCommand registerDeliveryCommand) {
        return orderRepository.findByIdPessimistic(registerDeliveryCommand.orderId())
            .orElseThrow(() -> new NotFoundOrderException("존재하지 않는 주문입니다."));
    }

    private void checkUserHasRegisterDeliveryAuthority(final Long userId) {
        User user = findUserByUserId(userId);
        if (!user.isEmployee()) {
            throw new UnauthorizedDeliveryException("권환이 없습니다.");
        }
    }

    private Delivery findDeliveryByOrderWithOrder(final Long orderId) {
        return deliveryRepository.findByOrderIdWithOrder(orderId)
                .orElseThrow(() -> new NotFoundDeliveryException("존재하지 않는 배달입니다."));
    }
    private void checkAuthority(final Delivery delivery, final User user) {
        if (!delivery.isOwnByUser(user)) {
            throw new UnauthorizedDeliveryException("권한이 없습니다.");
        }
    }
}

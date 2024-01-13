package com.group.Bmart.domain.payment.repository;

import com.group.Bmart.domain.payment.Payment;
import com.group.Bmart.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByOrder_UuidAndUser_UserId(String uuid, Long userId);

    void deleteByUser(User user);
}

package com.group.Bmart.domain.coupon.repository;

import com.group.Bmart.domain.coupon.Coupon;
import com.group.Bmart.domain.coupon.UserCoupon;
import com.group.Bmart.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface UserCouponRepository extends JpaRepository<UserCoupon, Long> {

    @Query("SELECT uc FROM UserCoupon uc JOIN FETCH uc.coupon c WHERE uc.userCouponId = :userCouponId")
    Optional<UserCoupon> findByIdWithCoupon(@Param("userCouponId") Long userCouponId);

    boolean existsByUserAndCoupon(User user, Coupon coupon);

    @Query("SELECT uc FROM UserCoupon uc JOIN FETCH uc.coupon c "
        + "WHERE uc.user = :user AND uc.isUsed = :isUsed AND c.endAt >= :currentDate")
    List<UserCoupon> findByUserAndIsUsedAndCouponEndAtAfter(
        @Param("user") User user,
        @Param("isUsed") boolean isUsed,
        @Param("currentDate") LocalDate currentDate);

    void deleteByUser(User findUser);
}


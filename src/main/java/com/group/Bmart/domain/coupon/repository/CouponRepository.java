package com.group.Bmart.domain.coupon.repository;

import com.group.Bmart.domain.coupon.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface CouponRepository extends JpaRepository<Coupon, Long> {

    List<Coupon> findByEndAtGreaterThanEqual(LocalDate currentDate);

    List<Coupon> findByEndAtBefore(LocalDate currentDate);

}



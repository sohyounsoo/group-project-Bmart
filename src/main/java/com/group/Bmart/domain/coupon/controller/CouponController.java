package com.group.Bmart.domain.coupon.controller;

import com.group.Bmart.domain.coupon.controller.request.RegisterCouponRequest;
import com.group.Bmart.domain.coupon.exception.CouponException;
import com.group.Bmart.domain.coupon.service.CouponService;
import com.group.Bmart.domain.coupon.service.request.RegisterCouponCommand;
import com.group.Bmart.domain.coupon.service.request.RegisterUserCouponCommand;
import com.group.Bmart.domain.coupon.service.response.FindCouponsResponse;
import com.group.Bmart.domain.coupon.service.response.FindIssuedCouponsResponse;
import com.group.Bmart.global.auth.LoginUser;
import com.group.Bmart.global.util.ErrorTemplate;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class CouponController {

    private final CouponService couponService;

    @PostMapping("/coupons")
    public ResponseEntity<Void> createCoupon(
        @Valid @RequestBody RegisterCouponRequest registerCouponRequest) {
        RegisterCouponCommand registerCouponCommand = RegisterCouponCommand.from(
            registerCouponRequest);
        Long couponId = couponService.createCoupon(registerCouponCommand);
        URI location = URI.create("/api/v1/coupons/" + couponId);
        return ResponseEntity.created(location).build();
    }

    @PostMapping("/my-coupons/{couponId}")
    public ResponseEntity<Void> RegisterUserCoupon(
        @PathVariable final Long couponId,
        @LoginUser final Long userId
    ) {
        RegisterUserCouponCommand registerUserCouponCommand = RegisterUserCouponCommand.of(userId,
            couponId);
        Long userCouponId = couponService.registerUserCoupon(registerUserCouponCommand);
        URI location = URI.create("/api/v1/my-coupons/" + userCouponId);
        return ResponseEntity.created(location).build();
    }

    @GetMapping("/coupons")
    public ResponseEntity<FindCouponsResponse> findCoupons() {
        FindCouponsResponse findCouponsResponse = couponService.findCoupons();
        return ResponseEntity.ok(findCouponsResponse);
    }

    @GetMapping("/my-coupons")
    public ResponseEntity<FindIssuedCouponsResponse> findIssuedCoupons(
        @LoginUser final Long userId
    ) {
        FindIssuedCouponsResponse findIssuedCouponsResponse = couponService.findIssuedCoupons(
            userId);
        return ResponseEntity.ok(findIssuedCouponsResponse);
    }

    @ExceptionHandler(CouponException.class)
    public ResponseEntity<ErrorTemplate> handleException(
        final CouponException couponException) {
        log.info(couponException.getMessage());
        return ResponseEntity.badRequest()
            .body(ErrorTemplate.of(couponException.getMessage()));
    }
}


package com.group.Bmart.domain.payment.controller;

import com.group.Bmart.domain.payment.service.PaymentClient;
import com.group.Bmart.domain.payment.service.PaymentService;
import com.group.Bmart.domain.payment.service.response.PaymentRequestResponse;
import com.group.Bmart.domain.payment.service.response.PaymentResponse;
import com.group.Bmart.global.auth.LoginUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/pays")
public class PaymentController {

    private final PaymentService paymentService;
    private final PaymentClient paymentClient;

    @PostMapping("/{orderId}")
    public ResponseEntity<PaymentRequestResponse> pay(
        @PathVariable Long orderId,
        @LoginUser Long userId
    ) {
        return ResponseEntity.ok(paymentService.pay(orderId, userId));
    }

    @GetMapping("/toss/success")
    public ResponseEntity<PaymentResponse> paySuccess(
        @RequestParam("orderId") String uuid,
        @RequestParam("paymentKey") String paymentKey,
        @RequestParam("amount") Integer amount,
        @LoginUser Long userId
    ) {
        paymentClient.confirmPayment(uuid, paymentKey, amount);

        return ResponseEntity.ok(
            paymentService.processSuccessPayment(userId, uuid, paymentKey, amount));
    }

    @GetMapping("/toss/fail")
    public ResponseEntity<PaymentResponse> payFail(
        @RequestParam("orderId") String uuid,
        @RequestParam("message") String errorMessage,
        @LoginUser Long userId
    ) {
        return ResponseEntity.ok(
            paymentService.processFailPayment(userId, uuid, errorMessage));
    }
}

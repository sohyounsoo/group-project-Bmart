package com.group.Bmart.domain.delivery.controller;

import com.group.Bmart.domain.delivery.controller.request.RegisterDeliveryRequest;
import com.group.Bmart.domain.delivery.service.DeliveryService;
import com.group.Bmart.domain.delivery.service.request.FindDeliveryByOrderCommand;
import com.group.Bmart.domain.delivery.service.request.RegisterDeliveryCommand;
import com.group.Bmart.domain.delivery.service.response.FindDeliveryByOrderResponse;
import com.group.Bmart.global.auth.LoginUser;
import com.group.Bmart.global.dto.CommonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class DeliveryController {

    private static final String BASE_URI = "/api/v1/deliveries/";

    private final DeliveryService deliveryService;

    @PostMapping("/orders/{orderId}/deliveries")
    public ResponseEntity<Void> registerDelivery(
            @PathVariable final Long orderId,
            @RequestBody final RegisterDeliveryRequest registerDeliveryRequest,
            @LoginUser final Long userId) {
        RegisterDeliveryCommand registerDeliveryCommand
                = RegisterDeliveryCommand.of(orderId, userId,
                registerDeliveryRequest.estimateMinutes());

        Long deliveryId = deliveryService.registerDelivery(registerDeliveryCommand);

        URI location = URI.create(BASE_URI + deliveryId);
        return ResponseEntity.created(location).build();
    }

    @GetMapping("/orders/{orderId}/deliveries")
    public ResponseEntity<CommonResponse> findDeliveryByOrder(
          @PathVariable final Long orderId,
          @LoginUser final Long userId) {

        FindDeliveryByOrderCommand findDeliveryByOrderCommand
            = FindDeliveryByOrderCommand.of(orderId, userId);
        FindDeliveryByOrderResponse findDeliveryByOrderResponse
            = deliveryService.findDeliverByOrder(findDeliveryByOrderCommand);


        CommonResponse response = CommonResponse.builder()
                .success(true)
                .response("")
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}

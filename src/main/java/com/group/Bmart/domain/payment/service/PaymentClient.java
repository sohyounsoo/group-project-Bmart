package com.group.Bmart.domain.payment.service;

import com.group.Bmart.domain.payment.exception.PaymentFailException;
import com.group.Bmart.domain.payment.service.response.TossPaymentApiResponse;
import com.group.Bmart.global.infrastructure.ApiService;
import lombok.RequiredArgsConstructor;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
@RequiredArgsConstructor
public class PaymentClient {

    private final ApiService apiService;

    @Value("1234")
    private String secretKey;

    @Value("1234")
    private String confirmUrl;

    public void confirmPayment(final String uuid, final String paymentKey, final Integer amount) {
        HttpHeaders httpHeaders = getHttpHeaders();
        JSONObject params = getParams(uuid, paymentKey, amount);
        TossPaymentApiResponse paymentApiResponse = requestPaymentApi(httpHeaders, params);

        validatePaymentResult(paymentApiResponse);
    }

    private HttpHeaders getHttpHeaders() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setBasicAuth(getEncodeAuth());
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        return httpHeaders;
    }

    private String getEncodeAuth() {
        return new String(
            Base64.getEncoder()
                .encode((secretKey + ":").getBytes(StandardCharsets.UTF_8))
        );
    }

    private JSONObject getParams(String uuid, String paymentKey, Integer amount) {
        JSONObject params = new JSONObject();
        params.put("paymentKey", paymentKey);
        params.put("orderId", uuid);
        params.put("amount", amount);

        return params;
    }

    private TossPaymentApiResponse requestPaymentApi(HttpHeaders httpHeaders, JSONObject params) {
        return apiService.getResult(
            new HttpEntity<>(params, httpHeaders),
            confirmUrl,
            TossPaymentApiResponse.class
        );
    }

    private void validatePaymentResult(TossPaymentApiResponse paymentApiResponse) {
        if (!paymentApiResponse.status().equals("DONE")) {
            throw new PaymentFailException("결제가 실패되었습니다.");
        }
    }
}

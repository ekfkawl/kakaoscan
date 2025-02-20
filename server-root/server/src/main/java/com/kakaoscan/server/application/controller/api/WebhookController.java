package com.kakaoscan.server.application.controller.api;

import com.kakaoscan.server.application.controller.ApiEndpointPrefix;
import com.kakaoscan.server.application.dto.request.WebhookProductPaymentRequest;
import com.kakaoscan.server.application.dto.response.ApiResponse;
import com.kakaoscan.server.application.service.ProductService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Log4j2
@RequiredArgsConstructor
@RestController
@Tag(name = "WebHook", description = "WebHook API")
public class WebhookController extends ApiEndpointPrefix {
    private final ProductService productService;

    @PostMapping("/webhook/payment")
    public ResponseEntity<ApiResponse<Void>> handlePaymentNotification(@RequestBody WebhookProductPaymentRequest paymentRequest) {
        log.info("payment notification for order: {}", paymentRequest.getOrderNumber());

        productService.approve(Long.valueOf(paymentRequest.getOrderNumber()));

        return new ResponseEntity<>(ApiResponse.success(), HttpStatus.OK);
    }

    @PostMapping("/webhook/v2/payment")
    public ResponseEntity<Map<String, String>> handlePaymentNotificationV2(@RequestBody WebhookProductPaymentRequest paymentRequest) {
        log.info("payment notification for order: {}", paymentRequest.getOrderNumber());

        productService.approve(Long.valueOf(paymentRequest.getOrderNumber()));

        Map<String, String> response = new HashMap<>();
        response.put("status", "success");
        return ResponseEntity.ok(response);
    }
}

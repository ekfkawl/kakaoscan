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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RequiredArgsConstructor
@RestController
@Tag(name = "WebHook", description = "WebHook API")
public class WebhookController extends ApiEndpointPrefix {
    private final ProductService productService;

    @PostMapping("/webhook/payment")
    public ResponseEntity<ApiResponse<Void>> handlePaymentNotification(@RequestBody WebhookProductPaymentRequest paymentRequest) {
        log.info("payment notification for order: {}", paymentRequest.getOrderNumber());

        productService.approveProductTransaction(Long.valueOf(paymentRequest.getOrderNumber()));

        return new ResponseEntity<>(ApiResponse.success(), HttpStatus.OK);
    }
}

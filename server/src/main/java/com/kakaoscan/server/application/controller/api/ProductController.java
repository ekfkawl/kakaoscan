package com.kakaoscan.server.application.controller.api;

import com.kakaoscan.server.application.controller.ApiEndpointPrefix;
import com.kakaoscan.server.application.dto.response.ApiResponse;
import com.kakaoscan.server.application.dto.response.ProductTransactions;
import com.kakaoscan.server.application.service.ProductService;
import com.kakaoscan.server.domain.product.model.PaymentRequest;
import com.kakaoscan.server.domain.user.model.CustomUserDetails;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@Tag(name = "Product", description = "Product API")
public class ProductController extends ApiEndpointPrefix {
    private final ProductService productService;

    @PostMapping("/payment")
    public ResponseEntity<ApiResponse<Void>> requestPayment(@RequestBody @Valid PaymentRequest paymentRequest, @AuthenticationPrincipal CustomUserDetails userDetails) {
        productService.request(userDetails.getId(), paymentRequest);

        return new ResponseEntity<>(ApiResponse.success(), HttpStatus.OK);
    }


    @PutMapping("/payment")
    public ResponseEntity<ApiResponse<Void>> cancelPointPayment(@RequestBody Map<String, Long> payload) {
        Long transactionId = payload.get("transactionId");
        productService.cancelRequest(transactionId);

        return new ResponseEntity<>(ApiResponse.success(), HttpStatus.OK);
    }

    @GetMapping("/product/transactions")
    public ResponseEntity<ApiResponse<ProductTransactions>> findProductTransactions(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                                                                    @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                                                                                    @AuthenticationPrincipal CustomUserDetails userDetails) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

        ProductTransactions transactions = productService.findProductTransactionsByPointWallet(userDetails.getUsername(), startDateTime, endDateTime);

        return new ResponseEntity<>(ApiResponse.success(transactions), HttpStatus.OK);
    }
}

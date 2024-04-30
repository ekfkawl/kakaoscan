package com.kakaoscan.server.application.controller.api;

import com.kakaoscan.server.application.controller.ApiEndpointPrefix;
import com.kakaoscan.server.application.dto.request.PointPaymentRequest;
import com.kakaoscan.server.application.dto.response.ApiResponse;
import com.kakaoscan.server.application.dto.response.TargetSearchCost;
import com.kakaoscan.server.application.service.PointService;
import com.kakaoscan.server.common.validation.ValidationPatterns;
import com.kakaoscan.server.domain.point.model.SearchCost;
import com.kakaoscan.server.domain.user.model.CustomUserDetails;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RequiredArgsConstructor
@RestController
@Tag(name = "Point", description = "Point API")
public class PointController extends ApiEndpointPrefix {
    private final PointService pointService;

    @GetMapping("/search-cost")
    public ResponseEntity<ApiResponse<TargetSearchCost>> getTargetSearchCost(@RequestParam String targetPhoneNumber, @RequestParam Boolean isId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        String replaceTargetPhoneNumber = targetPhoneNumber.trim().replace("-", "");
        if (!isId && !replaceTargetPhoneNumber.matches(ValidationPatterns.PHONE_NUMBER)) {
            return new ResponseEntity<>(ApiResponse.failure("message content is not a phone number format"), HttpStatus.BAD_REQUEST);
        }

        if (isId && !replaceTargetPhoneNumber.matches(ValidationPatterns.KAKAO_ID)) {
            return new ResponseEntity<>(ApiResponse.failure("message content is not a kakao id format"), HttpStatus.BAD_REQUEST);
        }

        SearchCost searchCost = pointService.getAndCacheTargetSearchCost(userDetails.getEmail(), isId ? "@".concat(replaceTargetPhoneNumber) : replaceTargetPhoneNumber);
        return new ResponseEntity<>(ApiResponse.success(searchCost.convertToTargetSearchCost()), HttpStatus.OK);
    }

    @PostMapping("/payment")
    public ResponseEntity<ApiResponse<Void>> pendPointPayment(@RequestBody @Valid PointPaymentRequest paymentRequest, @AuthenticationPrincipal CustomUserDetails userDetails) {
        if (pointService.pendPointPayment(userDetails.getEmail(), paymentRequest)){
            return new ResponseEntity<>(ApiResponse.success(), HttpStatus.OK);
        }

        return new ResponseEntity<>(ApiResponse.failure("이미 결제 진행 중 입니다."), HttpStatus.OK);
    }

    @PutMapping("/payment")
    public ResponseEntity<ApiResponse<Void>> cancelPointPayment(@RequestBody Map<String, Long> payload, @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long transactionId = payload.get("transactionId");
        pointService.cancelPointPayment(userDetails.getEmail(), transactionId);

        return new ResponseEntity<>(ApiResponse.success(), HttpStatus.OK);
    }
}

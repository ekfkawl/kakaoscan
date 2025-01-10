package com.kakaoscan.server.application.controller.api;

import com.kakaoscan.server.application.controller.ApiEndpointPrefix;
import com.kakaoscan.server.application.dto.response.ApiResponse;
import com.kakaoscan.server.application.dto.response.TargetSearchCost;
import com.kakaoscan.server.application.service.PointService;
import com.kakaoscan.server.common.validation.ValidationPatterns;
import com.kakaoscan.server.domain.point.model.SearchCost;
import com.kakaoscan.server.domain.user.model.CustomUserDetails;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

        SearchCost searchCost = pointService.getTargetSearchCost(userDetails.getEmail(), isId ? "@".concat(replaceTargetPhoneNumber) : replaceTargetPhoneNumber);
        return new ResponseEntity<>(ApiResponse.success(searchCost.convertToTargetSearchCost()), HttpStatus.OK);
    }
}

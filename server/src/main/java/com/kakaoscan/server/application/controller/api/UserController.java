package com.kakaoscan.server.application.controller.api;

import com.kakaoscan.server.application.controller.ApiPathPrefix;
import com.kakaoscan.server.application.dto.response.ApiResponse;
import com.kakaoscan.server.application.dto.request.RegisterRequest;
import com.kakaoscan.server.application.port.UserPort;
import com.kakaoscan.server.infrastructure.service.RateLimitService;
import com.kakaoscan.server.infrastructure.utils.WebUtils;
import io.github.bucket4j.Bucket;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

@RequiredArgsConstructor
@RestController
@Tag(name = "User", description = "User API")
public class UserController extends ApiPathPrefix {
    private final UserPort userPort;
    private final RateLimitService rateLimitService;

    @Value("${verify.replace}")
    private String verifyReplace;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> register(@RequestBody @Valid RegisterRequest registerRequest, HttpServletRequest request) {
        Bucket bucket = rateLimitService.resolveBucket(WebUtils.getRemoteAddress(request), 10, Duration.ofHours(1));
        if (bucket.tryConsume(1)) {
            return ResponseEntity.ok(userPort.register(registerRequest));
        }else {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body(new ApiResponse(false, "too many requests"));
        }
    }

    @GetMapping("/verify/{token}")
    public String consumeVerificationToken(@PathVariable("token") String token) {
        ApiResponse apiResponse = userPort.consumeVerificationToken(token);
        if (apiResponse.isSuccess()) {
            return String.format("<script>location.replace('%s')</script>", verifyReplace);
        }else {
            return apiResponse.getMessage();
        }
    }
    
}

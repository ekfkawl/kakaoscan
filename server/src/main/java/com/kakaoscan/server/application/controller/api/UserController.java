package com.kakaoscan.server.application.controller.api;

import com.kakaoscan.server.application.controller.ApiEndpointPrefix;
import com.kakaoscan.server.application.dto.request.RegisterRequest;
import com.kakaoscan.server.application.dto.response.ApiResponse;
import com.kakaoscan.server.application.port.PointPort;
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
public class UserController extends ApiEndpointPrefix {
    private final UserPort userPort;
    private final RateLimitService rateLimitService;
    private final PointPort pointPort;

    @Value("${verify.replace}")
    private String verifyReplace;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Void>> register(@RequestBody @Valid RegisterRequest registerRequest, HttpServletRequest request) {
        Bucket bucket = rateLimitService.resolveBucket(WebUtils.getRemoteAddress(request), 10, Duration.ofHours(1));
        if (bucket.tryConsume(1)) {
            return new ResponseEntity<>(userPort.register(registerRequest), HttpStatus.OK);
        }else {
            return new ResponseEntity<>(ApiResponse.failure("too many requests"), HttpStatus.TOO_MANY_REQUESTS);
        }
    }

    @GetMapping("/verify/{token}")
    public String consumeVerificationToken(@PathVariable("token") String token) {
        ApiResponse<Void> apiResponse = userPort.consumeVerificationToken(token);
        if (apiResponse.isSuccess()) {
            return String.format("<script>location.replace('%s')</script>", verifyReplace);
        }else {
            return apiResponse.getMessage();
        }
    }
}

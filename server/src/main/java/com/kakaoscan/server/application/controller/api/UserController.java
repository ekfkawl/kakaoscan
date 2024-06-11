package com.kakaoscan.server.application.controller.api;

import com.kakaoscan.server.application.controller.ApiEndpointPrefix;
import com.kakaoscan.server.application.dto.request.ChangePasswordRequest;
import com.kakaoscan.server.application.dto.request.RegisterRequest;
import com.kakaoscan.server.application.dto.response.ApiResponse;
import com.kakaoscan.server.application.dto.response.UserData;
import com.kakaoscan.server.application.service.UserService;
import com.kakaoscan.server.domain.user.enums.AuthenticationType;
import com.kakaoscan.server.domain.user.model.CustomUserDetails;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

@RequiredArgsConstructor
@RestController
@Tag(name = "User", description = "User API")
public class UserController extends ApiEndpointPrefix {
    private final UserService userService;
    private final RateLimitService rateLimitService;

    @Value("${verify.replace}")
    private String verifyReplace;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Void>> register(@RequestBody @Valid RegisterRequest registerRequest, HttpServletRequest request) {
        Bucket bucket = rateLimitService.resolveBucket(WebUtils.getRemoteAddress(request), 10, Duration.ofHours(1));
        if (bucket.tryConsume(1)) {
            return new ResponseEntity<>(userService.register(registerRequest), HttpStatus.OK);
        }else {
            return new ResponseEntity<>(ApiResponse.failure("too many requests"), HttpStatus.TOO_MANY_REQUESTS);
        }
    }

    @PutMapping("/user/password")
    public ResponseEntity<ApiResponse<Void>> changePassword(@RequestBody @Valid ChangePasswordRequest passwordRequest, @AuthenticationPrincipal CustomUserDetails userDetails) {
        UserData userData = userDetails.convertToUserData();
        if (userData.getAuthenticationType() == AuthenticationType.LOCAL) {
            userService.changePassword(userDetails.getEmail(), passwordRequest.getPassword());
            return new ResponseEntity<>(ApiResponse.success(), HttpStatus.OK);
        }

        return new ResponseEntity<>(ApiResponse.failure("OAuth 계정은 비밀번호를 변경할 수 없습니다."), HttpStatus.OK);
    }

    @GetMapping("/verify/{token}")
    public String consumeVerificationToken(@PathVariable("token") String token) {
        ApiResponse<Void> apiResponse = userService.consumeVerificationToken(token);
        if (apiResponse.isSuccess()) {
            return String.format("<script>location.replace('%s')</script>", verifyReplace);
        }else {
            return apiResponse.getMessage();
        }
    }

    @DeleteMapping("/user")
    public ResponseEntity<ApiResponse<Void>> delete(@AuthenticationPrincipal CustomUserDetails userDetails) {
        userService.delete(userDetails.getEmail());

        return new ResponseEntity<>(ApiResponse.success(), HttpStatus.OK);
    }
}

package com.kakaoscan.server.application.controller;

import com.kakaoscan.server.application.dto.ApiResponse;
import com.kakaoscan.server.application.dto.RegisterRequest;
import com.kakaoscan.server.application.port.UserPort;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@Tag(name = "User", description = "User API")
public class UserController extends ApiPathPrefix {
    private final UserPort userPort;

    @Value("${verify.replace}")
    private String verifyReplace;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> register(@RequestBody @Valid RegisterRequest registerRequest) {
        return ResponseEntity.ok(userPort.register(registerRequest));
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

package com.kakaoscan.server.application.controller;

import com.kakaoscan.server.application.dto.LoginRequest;
import com.kakaoscan.server.application.dto.LoginResponse;
import com.kakaoscan.server.application.port.AuthPort;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping
public class AuthController {
    private final AuthPort authPort;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticateUser(@RequestBody LoginRequest loginRequest) {
        LoginResponse result = authPort.authenticate(loginRequest);
        return ResponseEntity.ok(new LoginResponse(result.getAccessToken(), result.getRefreshToken()));
    }
}
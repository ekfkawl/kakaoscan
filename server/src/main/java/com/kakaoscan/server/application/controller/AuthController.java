package com.kakaoscan.server.application.controller;

import com.kakaoscan.server.application.dto.LoginRequest;
import com.kakaoscan.server.application.dto.LoginResponse;
import com.kakaoscan.server.application.port.AuthPort;
import com.kakaoscan.server.infrastructure.security.JwtTokenProvider;
import com.kakaoscan.server.infrastructure.security.JwtTokenUtils;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

@RequiredArgsConstructor
@RestController
public class AuthController extends ApiPathPrefix {
    private final JwtTokenUtils jwtTokenUtils;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthPort authPort;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticateUser(@RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        LoginResponse loginResponse = authPort.authenticate(loginRequest);

        jwtTokenUtils.saveRefreshTokenInCookie(loginResponse.getRefreshToken(), response);

        return ResponseEntity.ok(new LoginResponse(loginResponse.getAccessToken(), loginResponse.getRefreshToken()));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        jwtTokenUtils.deleteRefreshTokenFromCookie(response);

        return ResponseEntity.ok(Collections.singletonMap("message", "logged out"));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(HttpServletRequest request) {
        String refreshToken = jwtTokenUtils.extractRefreshTokenFromCookie(request);
        if (refreshToken == null || !jwtTokenProvider.validateRefreshToken(refreshToken)) {
            throw new JwtException("refresh token missing or invalid");
        }

        Authentication authentication;
        try {
            authentication = jwtTokenProvider.getAuthentication(refreshToken);
        } catch (JwtException e) {
            throw new JwtException("failed to authenticate using the provided refresh token");
        }

        String newAccessToken = jwtTokenProvider.createAccessToken(authentication);

        return ResponseEntity.ok(new LoginResponse(newAccessToken, refreshToken));
    }

}
package com.kakaoscan.server.application.service;

import com.kakaoscan.server.application.dto.response.LoginResponse;
import com.kakaoscan.server.application.dto.response.UserData;
import com.kakaoscan.server.domain.user.model.CustomUserDetails;
import com.kakaoscan.server.infrastructure.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final JwtTokenProvider jwtTokenProvider;

    public LoginResponse createJwtToken(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        UserData userData;

        if (principal instanceof CustomUserDetails) {
            userData = ((CustomUserDetails) principal).convertToUserData();
        }else {
            throw new IllegalArgumentException("unknown principal type error");
        }

        String accessToken = jwtTokenProvider.createAccessToken(authentication);
        String refreshToken = jwtTokenProvider.createRefreshToken(authentication);

        return new LoginResponse(accessToken, refreshToken, userData);
    }
}

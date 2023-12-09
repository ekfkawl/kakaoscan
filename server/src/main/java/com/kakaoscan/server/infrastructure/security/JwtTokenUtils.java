package com.kakaoscan.server.infrastructure.security;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@RequiredArgsConstructor
@Component
public class JwtTokenUtils {
    private static final String refreshTokenKey = "refreshToken";
    private final Environment env;

    public void saveRefreshTokenInCookie(String refreshToken, HttpServletResponse response) {
        Cookie refreshCookie = new Cookie(refreshTokenKey, refreshToken);
        if (Arrays.asList(env.getActiveProfiles()).contains("prod")) {
            refreshCookie.setSecure(true);
        }
        refreshCookie.setHttpOnly(true);
        refreshCookie.setPath("/");
        response.addCookie(refreshCookie);
    }

    public void deleteRefreshTokenFromCookie(HttpServletResponse response) {
        Cookie refreshCookie = new Cookie(refreshTokenKey, null);
        refreshCookie.setMaxAge(0);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setPath("/");
        response.addCookie(refreshCookie);
    }

    public String extractRefreshTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }

        return Arrays.stream(cookies)
                .filter(cookie -> refreshTokenKey.equals(cookie.getName()))
                .findFirst()
                .map(Cookie::getValue)
                .orElse(null);
    }

}

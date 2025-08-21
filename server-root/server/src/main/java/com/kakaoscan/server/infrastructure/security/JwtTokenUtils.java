package com.kakaoscan.server.infrastructure.security;

import com.kakaoscan.server.infrastructure.utils.ProfileUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Arrays;

@RequiredArgsConstructor
@Component
public class JwtTokenUtils {
    private static final String refreshTokenKey = "refreshToken";
    private final ProfileUtils profileUtils;

    public void saveRefreshTokenInCookie(String refreshToken, HttpServletResponse response) {
        Cookie refreshCookie = new Cookie(refreshTokenKey, refreshToken);
        refreshCookie.setSecure(profileUtils.isProd());
        refreshCookie.setMaxAge(12 * 60 * 60);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setPath("/");
        response.addCookie(refreshCookie);

        ResponseCookie hint = ResponseCookie.from("rt_present", "1")
                .httpOnly(false)
                .secure(profileUtils.isProd())
                .sameSite("Lax")
                .path("/")
                .maxAge(Duration.ofHours(12))
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, hint.toString());
    }

    public void deleteRefreshTokenFromCookie(HttpServletResponse response) {
        Cookie refreshCookie = new Cookie(refreshTokenKey, null);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(profileUtils.isProd());
        refreshCookie.setPath("/");
        refreshCookie.setMaxAge(0);
        response.addCookie(refreshCookie);

        ResponseCookie hintClear = ResponseCookie.from("rt_present", "")
                .httpOnly(false)
                .secure(profileUtils.isProd())
                .sameSite("Lax")
                .path("/")
                .maxAge(0)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, hintClear.toString());
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

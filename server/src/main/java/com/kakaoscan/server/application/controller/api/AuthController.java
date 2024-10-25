package com.kakaoscan.server.application.controller.api;

import com.kakaoscan.server.application.controller.ApiEndpointPrefix;
import com.kakaoscan.server.application.dto.request.LoginRequest;
import com.kakaoscan.server.application.dto.response.ApiResponse;
import com.kakaoscan.server.application.dto.response.LoginResponse;
import com.kakaoscan.server.application.dto.response.UserData;
import com.kakaoscan.server.application.port.AuthPort;
import com.kakaoscan.server.domain.user.model.CustomUserDetails;
import com.kakaoscan.server.domain.user.model.oauth2.GoogleOAuth2User;
import com.kakaoscan.server.domain.user.model.oauth2.NaverOAuth2User;
import com.kakaoscan.server.infrastructure.security.GoogleUserDetailsService;
import com.kakaoscan.server.infrastructure.security.JwtTokenProvider;
import com.kakaoscan.server.infrastructure.security.JwtTokenUtils;
import com.kakaoscan.server.infrastructure.security.NaverUserDetailsService;
import io.jsonwebtoken.JwtException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@Log4j2
@RequiredArgsConstructor
@RestController
@Tag(name = "Authenticate", description = "Authenticate API")
public class AuthController extends ApiEndpointPrefix {
    private final JwtTokenUtils jwtTokenUtils;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthPort authPort;
    private final GoogleUserDetailsService googleUserDetailsService;
    private final NaverUserDetailsService naverUserDetailsService;

    @PostMapping("/login")
    @Operation(summary = "Returns AccessToken and RefreshToken", description = "AccessToken validity is 1 hour")
    public ResponseEntity<ApiResponse<LoginResponse>> authenticateUser(@RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        LoginResponse loginResponse = authPort.authenticate(loginRequest);
        jwtTokenUtils.saveRefreshTokenInCookie(loginResponse.getRefreshToken(), response);

        return new ResponseEntity<>(ApiResponse.success(loginResponse), HttpStatus.OK);
    }

    @PostMapping("/login/oauth2/google")
    @Operation(summary = "Returns AccessToken and RefreshToken with Google AccessToken")
    public ResponseEntity<ApiResponse<LoginResponse>> authenticateGoogleUser(@RequestBody Map<String, String> googleToken, HttpServletResponse response) {
        String accessToken = googleToken.get("code");

        GoogleOAuth2User googleOAuth2User = googleUserDetailsService.loadUserByAccessToken(accessToken);
        CustomUserDetails customUserDetails = googleOAuth2User.convertToCustomUserDetails();

        LoginResponse loginResponse = authPort.authenticate(customUserDetails);
        jwtTokenUtils.saveRefreshTokenInCookie(loginResponse.getRefreshToken(), response);

        return new ResponseEntity<>(ApiResponse.success(loginResponse), HttpStatus.OK);
    }

    @GetMapping("/login/oauth2/naver")
    @Operation(summary = "Returns AccessToken and RefreshToken with Naver AccessToken")
    public void authenticateNaverUser(@RequestParam String code, @RequestParam String state, HttpServletResponse response) throws IOException {
        NaverOAuth2User naverOAuth2User = naverUserDetailsService.loadUserByCode(code, state);
        CustomUserDetails customUserDetails = naverOAuth2User.convertToCustomUserDetails();

        LoginResponse loginResponse = authPort.authenticate(customUserDetails);
        jwtTokenUtils.saveRefreshTokenInCookie(loginResponse.getRefreshToken(), response);

        response.sendRedirect(System.getenv("CURRENT_BASE_URL"));
    }

    @PostMapping("/logout")
    @Operation(summary = "Delete RefreshToken from cookie", description = "(AccessToken not blacklisted)")
    public ResponseEntity<ApiResponse<Void>> logout(HttpServletResponse response) {
        jwtTokenUtils.deleteRefreshTokenFromCookie(response);

        return new ResponseEntity<>(ApiResponse.success(), HttpStatus.OK);
    }

    @PostMapping("/refresh-token")
    @Operation(summary = "AccessToken reissue by RefreshToken")
    public ResponseEntity<ApiResponse<LoginResponse>> refreshToken(HttpServletRequest request) {
        String refreshToken = jwtTokenUtils.extractRefreshTokenFromCookie(request);
        if (refreshToken == null) {
            return new ResponseEntity<>(ApiResponse.failure("empty refresh token"), HttpStatus.UNAUTHORIZED);
        }

        if (!jwtTokenProvider.validateRefreshToken(refreshToken)) {
            throw new JwtException("invalid refresh token");
        }

        Authentication authentication = jwtTokenProvider.getAuthentication(refreshToken);

        String newAccessToken = jwtTokenProvider.createAccessToken(authentication);
        UserData userData = ((CustomUserDetails) authentication.getPrincipal()).convertToUserData();

        return new ResponseEntity<>(ApiResponse.success(new LoginResponse(newAccessToken, refreshToken, userData)), HttpStatus.OK);
    }
}
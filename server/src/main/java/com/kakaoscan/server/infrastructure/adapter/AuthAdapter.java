package com.kakaoscan.server.infrastructure.adapter;

import com.kakaoscan.server.application.dto.LoginRequest;
import com.kakaoscan.server.application.dto.LoginResponse;
import com.kakaoscan.server.application.exception.EmailNotVerifiedException;
import com.kakaoscan.server.application.port.AuthPort;
import com.kakaoscan.server.domain.user.CustomUserDetails;
import com.kakaoscan.server.infrastructure.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthAdapter implements AuthPort {
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    @Override
    public LoginResponse authenticate(LoginRequest request) throws BadCredentialsException, EmailNotVerifiedException {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        String accessToken = jwtTokenProvider.createAccessToken(authentication);
        String refreshToken = jwtTokenProvider.createRefreshToken(authentication);

        return new LoginResponse(accessToken, refreshToken, userDetails.convertToUserData());
    }
}

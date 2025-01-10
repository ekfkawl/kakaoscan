package com.kakaoscan.server.infrastructure.adapter;

import com.kakaoscan.server.application.dto.request.LoginRequest;
import com.kakaoscan.server.application.dto.response.LoginResponse;
import com.kakaoscan.server.application.exception.EmailNotVerifiedException;
import com.kakaoscan.server.application.port.AuthPort;
import com.kakaoscan.server.application.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthAdapter implements AuthPort {
    private final AuthenticationManager authenticationManager;
    private final AuthService authService;

    @Override
    public LoginResponse authenticate(LoginRequest request) throws BadCredentialsException, EmailNotVerifiedException {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail().toLowerCase(), request.getPassword()));

        return authService.createJwtToken(authentication);
    }

    @Override
    public LoginResponse authenticate(UserDetails userDetails) {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        return authService.createJwtToken(authentication);
    }
}

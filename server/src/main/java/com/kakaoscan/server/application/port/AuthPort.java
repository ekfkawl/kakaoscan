package com.kakaoscan.server.application.port;

import com.kakaoscan.server.application.dto.request.LoginRequest;
import com.kakaoscan.server.application.dto.response.LoginResponse;
import org.springframework.security.core.userdetails.UserDetails;

public interface AuthPort {
    LoginResponse authenticate(LoginRequest request);

    LoginResponse authenticate(UserDetails userDetails);
}

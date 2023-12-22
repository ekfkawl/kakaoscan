package com.kakaoscan.server.application.port;

import com.kakaoscan.server.application.dto.request.LoginRequest;
import com.kakaoscan.server.application.dto.response.LoginResponse;

public interface AuthPort {
    LoginResponse authenticate(LoginRequest request);
}

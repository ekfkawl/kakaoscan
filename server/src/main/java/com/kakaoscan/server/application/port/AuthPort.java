package com.kakaoscan.server.application.port;

import com.kakaoscan.server.application.dto.LoginRequest;
import com.kakaoscan.server.application.dto.LoginResponse;

public interface AuthPort {
    LoginResponse authenticate(LoginRequest request);
}

package com.kakaoscan.server.application.port;

import com.kakaoscan.server.application.dto.ApiResponse;
import com.kakaoscan.server.application.dto.RegisterRequest;
import com.kakaoscan.server.domain.user.model.EmailVerificationToken;
import com.kakaoscan.server.domain.user.model.User;

public interface UserPort {
    ApiResponse register(RegisterRequest registerRequest);

    EmailVerificationToken createVerificationToken(User user);

    ApiResponse consumeVerificationToken(String verificationToken);
}

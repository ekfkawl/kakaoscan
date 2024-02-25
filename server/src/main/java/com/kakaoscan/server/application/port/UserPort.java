package com.kakaoscan.server.application.port;

import com.kakaoscan.server.application.dto.request.RegisterRequest;
import com.kakaoscan.server.application.dto.response.ApiResponse;
import com.kakaoscan.server.domain.user.model.EmailVerificationToken;
import com.kakaoscan.server.domain.user.model.User;

public interface UserPort {
    ApiResponse<Void> register(RegisterRequest registerRequest);

    EmailVerificationToken createVerificationToken(User user);

    ApiResponse<Void> consumeVerificationToken(String verificationToken);
}

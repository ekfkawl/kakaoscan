package com.kakaoscan.server.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginResponse {
    private final String accessToken;
    private final String refreshToken;
    private final String tokenType = "Bearer";
}

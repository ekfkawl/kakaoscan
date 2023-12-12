package com.kakaoscan.server.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ApiResponse {
    private boolean success;
    private String message;
    private boolean hiddenMessage;

    public ApiResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
        this.hiddenMessage = false;
    }
}

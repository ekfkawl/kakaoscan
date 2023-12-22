package com.kakaoscan.server.application.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse {
    private boolean success;
    private String message;
    private boolean hiddenMessage;
    private Object data;

    public ApiResponse(boolean success) {
        this.success = success;
        this.message = null;
        this.hiddenMessage = true;
        this.data = null;
    }

    public ApiResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
        this.hiddenMessage = false;
        this.data = null;
    }

    public ApiResponse(boolean success, String message, boolean hiddenMessage) {
        this.success = success;
        this.message = message;
        this.hiddenMessage = hiddenMessage;
        this.data = null;
    }

    public ApiResponse(boolean success, String message, Object data) {
        this.success = success;
        this.message = message;
        this.hiddenMessage = false;
        this.data = data;
    }

    public ApiResponse(boolean success, Object data) {
        this.success = success;
        this.message = null;
        this.hiddenMessage = true;
        this.data = data;
    }
}

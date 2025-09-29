package com.kakaoscan.server.application.exception;

public class PayBadRequestException extends RuntimeException {
    public PayBadRequestException(String message) {
        super(message);
    }

    public PayBadRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}
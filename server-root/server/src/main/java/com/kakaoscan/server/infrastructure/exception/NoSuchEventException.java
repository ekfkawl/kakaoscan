package com.kakaoscan.server.infrastructure.exception;

public class NoSuchEventException extends RuntimeException {
    public NoSuchEventException(String message) {
        super(message);
    }

    public NoSuchEventException(String message, Throwable cause) {
        super(message, cause);
    }
}
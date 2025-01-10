package com.kakaoscan.server.application.exception;

public class DeletedUserException extends RuntimeException {
    public DeletedUserException(String message) {
        super(message);
    }

    public DeletedUserException(String message, Throwable cause) {
        super(message, cause);
    }
}

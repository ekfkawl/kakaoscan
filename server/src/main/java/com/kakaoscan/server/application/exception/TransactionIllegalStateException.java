package com.kakaoscan.server.application.exception;

public class TransactionIllegalStateException extends RuntimeException {
    public TransactionIllegalStateException(String message) {
        super(message);
    }

    public TransactionIllegalStateException(String message, Throwable cause) {
        super(message, cause);
    }
}
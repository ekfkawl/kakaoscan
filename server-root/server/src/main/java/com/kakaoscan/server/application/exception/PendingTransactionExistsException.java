package com.kakaoscan.server.application.exception;

public class PendingTransactionExistsException extends RuntimeException {
    public PendingTransactionExistsException(String message) {
        super(message);
    }

    public PendingTransactionExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}
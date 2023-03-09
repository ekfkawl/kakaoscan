package com.kakaoscan.profile.domain.server.exception;

public class InvalidAccess extends Exception {
    public InvalidAccess() {
        super();
    }

    public InvalidAccess(String message) {
        super(message);
    }
}

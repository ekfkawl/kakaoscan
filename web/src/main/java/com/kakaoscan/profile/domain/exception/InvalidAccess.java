package com.kakaoscan.profile.domain.exception;

public class InvalidAccess extends Exception {
    public InvalidAccess() {
        super();
    }

    public InvalidAccess(String message) {
        super(message);
    }
}

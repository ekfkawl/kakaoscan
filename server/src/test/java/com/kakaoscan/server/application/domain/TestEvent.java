package com.kakaoscan.server.application.domain;

import com.kakaoscan.server.domain.events.model.EventMetadata;

public class TestEvent extends EventMetadata {
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
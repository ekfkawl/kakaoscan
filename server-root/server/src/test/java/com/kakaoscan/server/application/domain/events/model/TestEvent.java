package com.kakaoscan.server.application.domain.events.model;

import com.kakaoscan.server.domain.events.model.common.EventMetadata;

public class TestEvent extends EventMetadata {
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
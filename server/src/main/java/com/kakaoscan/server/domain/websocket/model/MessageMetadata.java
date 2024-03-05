package com.kakaoscan.server.domain.websocket.model;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class MessageMetadata {
    private final String messageId;
    private final String email;

    public MessageMetadata(String email) {
        this.messageId = UUID.randomUUID().toString();
        this.email = email;
    }
}

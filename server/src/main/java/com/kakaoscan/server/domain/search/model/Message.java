package com.kakaoscan.server.domain.search.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class Message {
    private final String messageId;
    private final String email;
    private final String content;
    private final boolean hasNext;
    private final LocalDateTime createdAt;

    public Message(String email, String content, boolean hasNext) {
        this.messageId = UUID.randomUUID().toString();
        this.email = email;
        this.content = content;
        this.hasNext = hasNext;
        this.createdAt = LocalDateTime.now();
    }

    public Message(String email, String content) {
        this(email, content, true);
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OriginMessage {
        private String content;
    }
}

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
    private final boolean isJsonContent;
    private final boolean hasNext;
    private final LocalDateTime createdAt;

    public Message(String email, String content, boolean hasNext, boolean isJsonContent) {
        this.messageId = UUID.randomUUID().toString();
        this.email = email;
        this.content = content;
        this.isJsonContent = isJsonContent;
        this.hasNext = hasNext;
        this.createdAt = LocalDateTime.now();
    }

    public Message(String email, String content) {
        this(email, content, true, false);
    }

    public Message(String email, String content, boolean isJsonContent) {
        this(email, content, true, isJsonContent);
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OriginMessage {
        private String content;
    }
}

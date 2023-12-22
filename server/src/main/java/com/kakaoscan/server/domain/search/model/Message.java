package com.kakaoscan.server.domain.search.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class Message {
    private final String email;
    private final String content;
    private final LocalDateTime createdAt;

    public Message(String email, String content) {
        this.email = email;
        this.content = content;
        this.createdAt = LocalDateTime.now();
    }

    @Getter
    @AllArgsConstructor
    public static class OriginMessage {
        private String token;
        private String content;
    }
}

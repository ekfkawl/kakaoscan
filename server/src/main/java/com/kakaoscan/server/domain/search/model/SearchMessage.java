package com.kakaoscan.server.domain.search.model;

import com.kakaoscan.server.domain.websocket.model.MessageMetadata;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
public class SearchMessage extends MessageMetadata {
    private final String content;
    private final boolean isJsonContent;
    private final LocalDateTime createdAt;
    private LocalDateTime eventStartedAt;

    public SearchMessage(String email, String content, boolean isJsonContent) {
        super(email);
        this.content = content;
        this.isJsonContent = isJsonContent;
        this.createdAt = LocalDateTime.now();
        this.eventStartedAt = null;
    }

    public SearchMessage(String email, String content) {
        this(email, content, false);
    }

    public void setEventStartedAt() {
        this.eventStartedAt = LocalDateTime.now();
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OriginMessage {
        private String content;
    }
}

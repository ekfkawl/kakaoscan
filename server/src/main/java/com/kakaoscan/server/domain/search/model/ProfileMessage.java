package com.kakaoscan.server.domain.search.model;

import com.kakaoscan.server.domain.websocket.model.MessageMetadata;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
public class ProfileMessage extends MessageMetadata {
    private final String content;
    private final boolean isJsonContent;
    private final boolean hasNext;
    private final LocalDateTime createdAt;
    private LocalDateTime eventStartedAt;

    public ProfileMessage(String email, String content, boolean hasNext, boolean isJsonContent) {
        super(email);
        this.content = content;
        this.isJsonContent = isJsonContent;
        this.hasNext = hasNext;
        this.createdAt = LocalDateTime.now();
        this.eventStartedAt = null;
    }

    public ProfileMessage(String email, String content) {
        this(email, content, true, false);
    }

    public ProfileMessage(String email, String content, boolean isJsonContent) {
        this(email, content, true, isJsonContent);
    }

    public void createEventStartedAt() {
        this.eventStartedAt = LocalDateTime.now();
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OriginMessage {
        private String content;
    }
}

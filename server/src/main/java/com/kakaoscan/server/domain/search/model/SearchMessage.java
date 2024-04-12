package com.kakaoscan.server.domain.search.model;

import com.kakaoscan.server.domain.websocket.model.MessageMetadata;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
public class SearchMessage extends MessageMetadata {
    @Setter
    private String content;
    @Setter
    private String reconnectContent;
    private final boolean isJsonContent;
    private final boolean hasNext;
    private final LocalDateTime createdAt;
    private LocalDateTime eventStartedAt;

    public SearchMessage(String email, String content, boolean hasNext, boolean isJsonContent) {
        super(email);
        this.content = content;
        this.isJsonContent = isJsonContent;
        this.hasNext = hasNext;
        this.createdAt = LocalDateTime.now();
        this.eventStartedAt = null;
    }

    public SearchMessage(String email, String content, boolean hasNext) {
        this(email, content, hasNext, false);
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

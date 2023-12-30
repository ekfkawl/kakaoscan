package com.kakaoscan.server.domain.events;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class EventMetadata {
    protected String eventId;
    protected LocalDateTime createdAt;

    public EventMetadata() {
        this.eventId = UUID.randomUUID().toString();
        this.createdAt = LocalDateTime.now();
    }
}

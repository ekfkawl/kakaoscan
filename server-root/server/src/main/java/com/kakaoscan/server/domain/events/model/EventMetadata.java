package com.kakaoscan.server.domain.events.model;

import io.ekfkawl.model.EventBase;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class EventMetadata implements EventBase {
    protected String eventId;
    protected LocalDateTime createdAt;

    public EventMetadata() {
        this.eventId = UUID.randomUUID().toString();
        this.createdAt = LocalDateTime.now();
    }
}

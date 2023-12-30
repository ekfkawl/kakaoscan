package com.kakaoscan.server.application.port;

import com.kakaoscan.server.domain.events.EventMetadata;
import com.kakaoscan.server.domain.events.EventStatus;

import java.util.Optional;

public interface EventStatusPort {
    void setEventStatus(String eventId, EventStatus status);
    Optional<EventStatus> getEventStatus(String eventId);
    <T extends EventMetadata> Optional<EventStatus> getEventStatus(T event);
}

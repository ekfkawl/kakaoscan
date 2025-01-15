package com.kakaoscan.server.application.port;

import com.kakaoscan.server.domain.events.model.EventMetadata;
import io.ekfkawl.model.EventStatus;

import java.util.Optional;

public interface EventStatusPort {
    void setEventStatus(String eventId, EventStatus status);
    Optional<EventStatus> getEventStatus(String eventId);
    void deleteEventStatus(String eventId);
    <T extends EventMetadata> Optional<EventStatus> getEventStatus(T event);
}

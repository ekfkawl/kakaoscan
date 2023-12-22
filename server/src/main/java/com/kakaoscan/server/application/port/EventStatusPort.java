package com.kakaoscan.server.application.port;

import com.kakaoscan.server.domain.events.EventMetadata;
import com.kakaoscan.server.domain.events.EventStatus;

public interface EventStatusPort {
    void setEventStatus(String eventId, EventStatus status);
    EventStatus getEventStatus(String eventId);
    <T extends EventMetadata> EventStatus getEventStatus(T event);
}

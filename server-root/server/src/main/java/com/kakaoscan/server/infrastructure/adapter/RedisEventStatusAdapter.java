package com.kakaoscan.server.infrastructure.adapter;

import com.kakaoscan.server.application.port.CacheStorePort;
import com.kakaoscan.server.application.port.EventStatusPort;
import com.kakaoscan.server.domain.events.model.common.EventMetadata;
import io.ekfkawl.model.EventStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisEventStatusAdapter implements EventStatusPort {
    private final CacheStorePort<EventStatus> cacheStorePort;


    @Override
    public void setEventStatus(String eventId, EventStatus status) {
        cacheStorePort.put(eventId, status, 10, TimeUnit.MINUTES);
    }

    @Override
    public Optional<EventStatus> getEventStatus(String eventId) {
        EventStatus eventStatus = cacheStorePort.get(eventId, EventStatus.class);

        return (eventStatus == null) ? Optional.empty() : Optional.of(eventStatus);
    }

    @Override
    public void deleteEventStatus(String eventId) {
        cacheStorePort.deleteKey(eventId, EventStatus.class);
    }

    @Override
    public <T extends EventMetadata> Optional<EventStatus> getEventStatus(T event) {
        return getEventStatus(event.getEventId());
    }
}

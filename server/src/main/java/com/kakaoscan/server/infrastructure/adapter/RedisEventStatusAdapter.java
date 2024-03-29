package com.kakaoscan.server.infrastructure.adapter;

import com.kakaoscan.server.application.port.EventStatusPort;
import com.kakaoscan.server.domain.events.model.EventMetadata;
import com.kakaoscan.server.domain.events.model.EventStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisEventStatusAdapter implements EventStatusPort {
    private final RedisTemplate<String, EventStatus> redisTemplate;

    private static final String EVENT_KEY_PREFIX = "eventStatus:";

    @Override
    public void setEventStatus(String eventId, EventStatus status) {
        ValueOperations<String, EventStatus> ops = redisTemplate.opsForValue();

        ops.set(EVENT_KEY_PREFIX + eventId, status, 10, TimeUnit.MINUTES);
    }

    @Override
    public Optional<EventStatus> getEventStatus(String eventId) {
        ValueOperations<String, EventStatus> ops = redisTemplate.opsForValue();
        EventStatus eventStatus = ops.get(EVENT_KEY_PREFIX + eventId);

        return (eventStatus == null) ? Optional.empty() : Optional.of(eventStatus);
    }

    @Override
    public <T extends EventMetadata> Optional<EventStatus> getEventStatus(T event) {
        return getEventStatus(event.getEventId());
    }
}

package com.kakaoscan.server.infrastructure.redis.publisher;

import com.kakaoscan.server.application.port.EventStatusPort;
import com.kakaoscan.server.domain.events.EventMetadata;
import com.kakaoscan.server.domain.events.EventStatus;
import com.kakaoscan.server.domain.events.enums.EventStatusEnum;
import com.kakaoscan.server.infrastructure.serialization.JsonEventSerializer;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EventPublisher {
    private final StringRedisTemplate stringRedisTemplate;
    private final EventStatusPort eventStatusPort;

    public <T extends EventMetadata> void publish(String topic, T event) {
        String serializeEvent = JsonEventSerializer.serialize(event);
        stringRedisTemplate.convertAndSend(topic, serializeEvent);

        eventStatusPort.setEventStatus(event.getEventId(), new EventStatus(EventStatusEnum.WAITING));
    }
}

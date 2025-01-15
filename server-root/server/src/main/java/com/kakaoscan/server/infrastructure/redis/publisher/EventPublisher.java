package com.kakaoscan.server.infrastructure.redis.publisher;

import com.kakaoscan.server.domain.events.model.EventMetadata;
import io.ekfkawl.json.JsonEventSerializer;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EventPublisher {
    private final StringRedisTemplate stringRedisTemplate;

    public <T extends EventMetadata> void publish(String topic, T event) {
        String serializeEvent = JsonEventSerializer.serialize(event);
        stringRedisTemplate.convertAndSend(topic, serializeEvent);
    }
}

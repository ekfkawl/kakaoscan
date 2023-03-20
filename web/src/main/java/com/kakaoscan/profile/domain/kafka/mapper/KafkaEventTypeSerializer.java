package com.kakaoscan.profile.domain.kafka.mapper;

import com.kakaoscan.profile.domain.enums.KafkaEventType;
import org.apache.kafka.common.serialization.Serializer;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;

public class KafkaEventTypeSerializer implements Serializer<KafkaEventType> {

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
    }

    @Override
    public byte[] serialize(String topic, KafkaEventType data) {
        if (data == null) {
            return null;
        }
        String key = data.getValue().concat(":" + UUID.randomUUID());
        return key.getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public void close() {
    }
}
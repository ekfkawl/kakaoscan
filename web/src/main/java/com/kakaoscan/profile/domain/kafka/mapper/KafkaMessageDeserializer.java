package com.kakaoscan.profile.domain.kafka.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kakaoscan.profile.domain.model.KafkaMessage;

import java.io.IOException;

public class KafkaMessageDeserializer {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static KafkaMessage deserialize(String json) throws IOException {
        return mapper.readValue(json, KafkaMessage.class);
    }
}

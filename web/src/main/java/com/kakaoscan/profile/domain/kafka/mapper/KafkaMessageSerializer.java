package com.kakaoscan.profile.domain.kafka.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kakaoscan.profile.domain.model.KafkaMessage;
import org.apache.kafka.common.serialization.Serializer;

import java.util.Map;

public class KafkaMessageSerializer implements Serializer<KafkaMessage> {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
    }

    @Override
    public byte[] serialize(String topic, KafkaMessage data) {
        byte[] retVal = null;
        try {
            retVal = objectMapper.writeValueAsString(data).getBytes();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return retVal;
    }

    @Override
    public void close() {
    }
}
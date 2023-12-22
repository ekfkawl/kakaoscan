package com.kakaoscan.server.infrastructure.serialization;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kakaoscan.server.common.utils.ObjectMapperSingleton;

public class JsonDeserialize {
    private static final ObjectMapper objectMapper = ObjectMapperSingleton.getInstance();

    public static <T> T deserialize(String json, Class<T> clazz) {
        try {
            return objectMapper.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("error deserializing Json: ", e);
        }
    }
}

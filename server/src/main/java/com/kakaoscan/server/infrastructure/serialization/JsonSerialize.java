package com.kakaoscan.server.infrastructure.serialization;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kakaoscan.server.common.utils.ObjectMapperSingleton;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class JsonSerialize {
    private static final ObjectMapper objectMapper = ObjectMapperSingleton.getInstance();

    public static String serialize(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.error(e);
            throw new RuntimeException("error serializing Json: ", e);
        }
    }
}
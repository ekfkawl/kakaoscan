package io.ekfkawl.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import static io.ekfkawl.ExceptionSupportUtils.handleException;

@Slf4j
public class JsonSerialize {
    private static final ObjectMapper objectMapper = ObjectMapperSingleton.getInstance();

    public static String serialize(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            return handleException("error serializing Json: ", e);
        }
    }
}
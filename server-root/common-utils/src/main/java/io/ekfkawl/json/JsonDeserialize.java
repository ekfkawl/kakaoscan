package io.ekfkawl.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import static io.ekfkawl.ExceptionSupportUtils.handleException;

@Slf4j
public class JsonDeserialize {
    private static final ObjectMapper objectMapper = ObjectMapperSingleton.getInstance();

    public static <T> T deserialize(String json, Class<T> clazz) {
        try {
            return objectMapper.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            return handleException("error deserializing Json: ", e);
        }
    }
}

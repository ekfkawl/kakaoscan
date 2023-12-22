package com.kakaoscan.server.infrastructure.redis.subscriber;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kakaoscan.server.common.utils.ObjectMapperSingleton;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;

@Log4j2
public abstract class AbstractEventReceiver implements EventReceiver {

    private static final ObjectMapper objectMapper = ObjectMapperSingleton.getInstance();

    protected JsonNode parseMessage(String eventJson) throws IOException {
        return objectMapper.readTree(eventJson);
    }

    @Override
    public void receiveEvent(String eventJson) {
        try {
            JsonNode rootNode = parseMessage(eventJson);
            String eventType = rootNode.get("eventType").asText();
            String eventData = rootNode.get("data").toString();

            processEvent(eventType, eventData);
        } catch (JsonProcessingException e) {
            log.error("json parsing error: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("unknown event type: {}", e.getMessage());
        } catch (Exception e) {
            log.error("processing message error: {}", e.getMessage());
        }
    }

    protected abstract void processEvent(String eventType, String eventData);
}

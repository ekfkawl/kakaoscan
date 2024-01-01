package com.kakaoscan.server.infrastructure.serialization;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.kakaoscan.server.common.utils.ObjectMapperSingleton;
import com.kakaoscan.server.domain.events.model.EventMetadata;

public class JsonEventSerializer {
    private static final ObjectMapper objectMapper = ObjectMapperSingleton.getInstance();

    public static String serialize(EventMetadata event) {
        try {
            ObjectNode rootNode = objectMapper.createObjectNode();
            rootNode.put("eventType", event.getClass().getSimpleName());
            rootNode.set("data", objectMapper.valueToTree(event));

            return rootNode.toString();
        } catch (Exception e) {
            throw new RuntimeException("serialization error", e);
        }
    }
}

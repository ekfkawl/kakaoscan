package io.ekfkawl.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.ekfkawl.model.EventBase;

import static io.ekfkawl.ExceptionSupportUtils.handleException;


public class JsonEventSerializer {
    private static final ObjectMapper objectMapper = ObjectMapperSingleton.getInstance();

    public static String serialize(EventBase event) {
        try {
            ObjectNode rootNode = objectMapper.createObjectNode();
            rootNode.put("eventType", event.getClass().getSimpleName());
            rootNode.set("data", objectMapper.valueToTree(event));

            return rootNode.toString();
        } catch (Exception e) {
            return handleException("serialization error", e);
        }
    }
}

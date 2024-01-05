package com.kakaoscan.server.infrastructure.events.processor;

import com.kakaoscan.server.application.events.handlers.SearchEventHandler;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class EventProcessorFactory {
    private final Map<String, EventProcessor> processorMap = new HashMap<>();

    public EventProcessorFactory(ApplicationContext applicationContext) {
        if (applicationContext != null) {
            registerProcessor("SearchEvent", applicationContext.getBean(SearchEventHandler.class));
        }
    }

    public void registerProcessor(String eventType, EventProcessor eventProcessor) {
        processorMap.put(eventType, eventProcessor);
    }

    public EventProcessor getProcessor(String eventType) {
        EventProcessor processor = processorMap.get(eventType);
        if (processor == null) {
            throw new IllegalArgumentException("unknown event type error: " + eventType);
        }
        return processor;
    }
}
package com.kakaoscan.server.infrastructure.events.processor;

import com.kakaoscan.server.application.port.EventStatusPort;
import com.kakaoscan.server.infrastructure.events.handlers.SearchEventHandler;
import com.kakaoscan.server.infrastructure.events.handlers.SetStatusEventHandler;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class EventProcessorFactory {
    private final Map<String, EventProcessor> processorMap = new HashMap<>();
    private final EventStatusPort eventStatusPort;

    @PostConstruct
    public void init() {
        registerProcessor("SearchEvent", new SearchEventHandler());
        registerProcessor("SetStatusEvent", new SetStatusEventHandler(eventStatusPort));
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
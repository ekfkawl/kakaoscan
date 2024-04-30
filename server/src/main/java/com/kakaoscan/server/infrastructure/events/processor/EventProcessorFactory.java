package com.kakaoscan.server.infrastructure.events.processor;

import org.reflections.Reflections;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component
public class EventProcessorFactory {
    private final Map<String, EventProcessor> processorMap = new HashMap<>();

    public EventProcessorFactory(ApplicationContext applicationContext) {
        if (applicationContext == null) {
            throw new NullPointerException("application context is null");
        }

        Reflections reflections = new Reflections("com.kakaoscan.server.application.events.handlers");
        Set<Class<? extends EventProcessor>> classes = reflections.getSubTypesOf(EventProcessor.class);

        for (Class<? extends EventProcessor> clazz : classes) {
            if (clazz.equals(AbstractEventProcessor.class)) {
                continue;
            }

            String eventType = clazz.getSimpleName().replaceAll("Handler$", "");
            EventProcessor processor = applicationContext.getBean(clazz);
            processorMap.put(eventType, processor);
        }
    }

    public EventProcessor getProcessor(String eventType) {
        EventProcessor processor = processorMap.get(eventType);
        if (processor == null) {
            throw new IllegalArgumentException("unknown event type error: " + eventType);
        }
        return processor;
    }
}
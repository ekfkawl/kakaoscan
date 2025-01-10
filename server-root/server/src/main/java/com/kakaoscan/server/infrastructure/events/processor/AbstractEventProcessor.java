package com.kakaoscan.server.infrastructure.events.processor;

import com.kakaoscan.server.domain.events.model.EventMetadata;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import static com.kakaoscan.server.infrastructure.serialization.JsonDeserialize.deserialize;

public abstract class AbstractEventProcessor<T extends EventMetadata> implements EventProcessor {
    private final Class<T> eventType;

    protected AbstractEventProcessor(Class<T> eventType) {
        this.eventType = eventType;
    }

    @SuppressWarnings("unchecked")
    protected AbstractEventProcessor() {
        Type type = ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        this.eventType = (Class<T>) type;
    }

    @Override
    public void process(String eventData) {
        T event = deserialize(eventData, eventType);
        handleEvent(event);
    }

    protected abstract void handleEvent(T event);
}

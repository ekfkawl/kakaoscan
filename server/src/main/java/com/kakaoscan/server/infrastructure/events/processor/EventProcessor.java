package com.kakaoscan.server.infrastructure.events.processor;

public interface EventProcessor {
    void process(String eventData);
}
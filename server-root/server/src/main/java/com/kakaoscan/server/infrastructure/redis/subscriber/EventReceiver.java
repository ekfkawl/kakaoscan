package com.kakaoscan.server.infrastructure.redis.subscriber;

public interface EventReceiver {
    void receiveEvent(String eventJson);
}

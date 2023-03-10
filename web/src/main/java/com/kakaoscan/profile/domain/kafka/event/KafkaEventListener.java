package com.kakaoscan.profile.domain.kafka.event;

import lombok.extern.log4j.Log4j2;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@Log4j2
public class KafkaEventListener {
    @Async
    @EventListener
    public void onEvent(KafkaEvent event) {
        log.info("Received event with key: {}, value: {}, {}", event.getKey(), event.getValue().getType(), event.getValue().getMessage());
    }
}

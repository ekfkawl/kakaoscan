package com.kakaoscan.profile.domain.kafka.event;

import com.kakaoscan.profile.domain.service.UserHistoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Log4j2
public class KafkaEventListener {
    private final UserHistoryService userHistoryService;

    @Async
    @EventListener
    public void onEvent(KafkaEvent event) {

        switch (event.getValue().getType()) {
            case UPSERT:
                // email, phone, json
                userHistoryService.updateHistory(event.getKey(), event.getValue().getSubMessage(), event.getValue().getMessage());
                break;
        }

        log.info("Received event with key: {}, value: {}, {}", event.getKey(), event.getValue().getType(), event.getValue().getMessage());
    }
}

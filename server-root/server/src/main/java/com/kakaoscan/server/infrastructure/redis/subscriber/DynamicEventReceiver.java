package com.kakaoscan.server.infrastructure.redis.subscriber;

import com.kakaoscan.server.infrastructure.events.processor.EventProcessor;
import com.kakaoscan.server.infrastructure.events.processor.EventProcessorFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;

@RequiredArgsConstructor
@Component
public class DynamicEventReceiver extends AbstractEventReceiver {
    private final EventProcessorFactory eventProcessorFactory;
    private final ExecutorService taskExecutorService;

    @Override
    public void processEvent(String eventType, String eventData) {
        taskExecutorService.submit(() -> {
            EventProcessor processor = eventProcessorFactory.getProcessor(eventType);
            processor.process(eventData);
        });
    }
}

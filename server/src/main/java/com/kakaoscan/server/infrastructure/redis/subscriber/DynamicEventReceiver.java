package com.kakaoscan.server.infrastructure.redis.subscriber;

import com.kakaoscan.server.application.port.EventStatusPort;
import com.kakaoscan.server.domain.events.EventMetadata;
import com.kakaoscan.server.domain.events.EventStatus;
import com.kakaoscan.server.domain.events.enums.EventStatusEnum;
import com.kakaoscan.server.infrastructure.events.processor.EventProcessor;
import com.kakaoscan.server.infrastructure.events.processor.EventProcessorFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;

import static com.kakaoscan.server.infrastructure.serialization.JsonDeserialize.deserialize;

@RequiredArgsConstructor
@Component
public class DynamicEventReceiver extends AbstractEventReceiver {
    private final EventProcessorFactory eventProcessorFactory;
    private final ExecutorService taskExecutorService;
    private final EventStatusPort eventStatusPort;

    @Override
    public void processEvent(String eventType, String eventData) {
        EventMetadata eventMetadata = deserialize(eventData, EventMetadata.class);
        eventStatusPort.setEventStatus(eventMetadata.getEventId(), new EventStatus(EventStatusEnum.PROCESSING));

        EventProcessor processor = eventProcessorFactory.getProcessor(eventType);
        taskExecutorService.submit(() -> {
            processor.process(eventData);
        });
    }
}

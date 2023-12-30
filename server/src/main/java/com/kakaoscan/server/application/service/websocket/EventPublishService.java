package com.kakaoscan.server.application.service.websocket;

import com.kakaoscan.server.application.port.EventStatusPort;
import com.kakaoscan.server.domain.events.EventStatus;
import com.kakaoscan.server.domain.events.enums.EventStatusEnum;
import com.kakaoscan.server.domain.events.types.external.SearchEvent;
import com.kakaoscan.server.domain.search.model.Message;
import com.kakaoscan.server.infrastructure.redis.enums.Topics;
import com.kakaoscan.server.infrastructure.redis.publisher.EventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EventPublishService {

    private final EventPublisher eventPublisher;
    private final EventStatusPort eventStatusPort;

    public void publishSearchEvent(Topics topic, Message message) {
        SearchEvent event = SearchEvent.builder()
                .eventId(message.getMessageId())
                .email(message.getEmail())
                .phoneNumber(message.getContent())
                .build();

        eventStatusPort.setEventStatus(event.getEventId(), new EventStatus(EventStatusEnum.WAITING));
        eventPublisher.publish(topic.getTopic(), event);
    }
}

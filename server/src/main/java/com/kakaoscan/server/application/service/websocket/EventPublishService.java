package com.kakaoscan.server.application.service.websocket;

import com.kakaoscan.server.application.port.EventStatusPort;
import com.kakaoscan.server.domain.events.enums.EventStatusEnum;
import com.kakaoscan.server.domain.events.model.EventStatus;
import com.kakaoscan.server.domain.events.model.SearchEvent;
import com.kakaoscan.server.domain.search.model.SearchMessage;
import com.kakaoscan.server.infrastructure.redis.enums.Topics;
import com.kakaoscan.server.infrastructure.redis.publisher.EventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.kakaoscan.server.infrastructure.redis.enums.Topics.SEARCH_EVENT_TOPIC;

@Service
@RequiredArgsConstructor
public class EventPublishService {

    private final EventPublisher eventPublisher;
    private final EventStatusPort eventStatusPort;

    public void publishSearchEvent(Topics topic, SearchMessage searchMessage) {
        SearchEvent event = SearchEvent.builder()
                .eventId(searchMessage.getMessageId())
                .email(searchMessage.getEmail())
                .phoneNumber(searchMessage.getContent())
                .build();

        if (topic == SEARCH_EVENT_TOPIC) {
            eventStatusPort.setEventStatus(event.getEventId(), new EventStatus(EventStatusEnum.WAITING));
        }
        eventPublisher.publish(topic.getTopic(), event);
    }
}

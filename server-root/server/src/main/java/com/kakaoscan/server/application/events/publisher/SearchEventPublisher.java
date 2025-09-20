package com.kakaoscan.server.application.events.publisher;

import com.kakaoscan.server.application.port.EventStatusPort;
import com.kakaoscan.server.domain.events.model.SearchEvent;
import com.kakaoscan.server.domain.search.model.SearchMessage;
import com.kakaoscan.server.infrastructure.redis.enums.Topics;
import com.kakaoscan.server.infrastructure.redis.publisher.EventPublisher;
import io.ekfkawl.enums.EventStatusEnum;
import io.ekfkawl.model.EventStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@RequiredArgsConstructor
public class SearchEventPublisher {
    private final EventPublisher eventPublisher;
    private final EventStatusPort eventStatusPort;

    public void publish(Topics topic, SearchMessage searchMessage) {
        SearchEvent event = SearchEvent.builder()
                .eventId(searchMessage.getMessageId())
                .email(searchMessage.getEmail())
                .phoneNumber(searchMessage.getContent())
                .isId(searchMessage.isId())
                .build();

        eventStatusPort.setEventStatus(event.getEventId(), new EventStatus(EventStatusEnum.WAITING));
        eventPublisher.publish(topic.getTopic(), event);
    }
}

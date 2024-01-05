package com.kakaoscan.server.application.events.handlers;

import com.kakaoscan.server.application.port.EventStatusPort;
import com.kakaoscan.server.application.service.websocket.StompMessageDispatcher;
import com.kakaoscan.server.domain.events.model.EventStatus;
import com.kakaoscan.server.domain.events.model.SearchEvent;
import com.kakaoscan.server.domain.search.model.Message;
import com.kakaoscan.server.domain.search.queue.QueueAggregate;
import com.kakaoscan.server.infrastructure.events.processor.AbstractEventProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static com.kakaoscan.server.domain.events.enums.EventStatusEnum.PROCESSING;
import static com.kakaoscan.server.domain.events.enums.EventStatusEnum.WAITING;
import static com.kakaoscan.server.infrastructure.constants.ResponseMessages.SEARCH_STARTING;
import static com.kakaoscan.server.infrastructure.constants.ResponseMessages.SEARCH_WAITING;

@Component
@RequiredArgsConstructor
public class SearchEventHandler extends AbstractEventProcessor<SearchEvent> {
    private final EventStatusPort eventStatusPort;
    private final QueueAggregate queue;
    private final StompMessageDispatcher messageDispatcher;

    @Override
    protected void handleEvent(SearchEvent event) {
        Optional<EventStatus> optionalEventStatus = eventStatusPort.getEventStatus(event.getEventId());
        if (optionalEventStatus.isEmpty()) {
            return;
        }

        EventStatus status = optionalEventStatus.get();
        final String responseMessage = switch (status.getStatus()) {
            case WAITING -> SEARCH_WAITING;
            case PROCESSING -> (status.getMessage() == null || status.getMessage().isEmpty()) ? SEARCH_STARTING : status.getMessage();
            case SUCCESS, FAILURE -> {
                queue.remove(event.getEventId());
                yield status.getMessage();
            }
        };
        boolean hasNext = status.getStatus() == WAITING || status.getStatus() == PROCESSING;
        messageDispatcher.sendToUser(new Message(event.getEmail(), responseMessage, hasNext));
    }
}
package com.kakaoscan.server.application.service.websocket.search;

import com.kakaoscan.server.application.events.publisher.SearchEventPublisher;
import com.kakaoscan.server.application.port.EventStatusPort;
import com.kakaoscan.server.application.service.websocket.StompMessageDispatcher;
import com.kakaoscan.server.domain.search.model.SearchMessage;
import com.kakaoscan.server.infrastructure.websocket.queue.SearchInMemoryQueue;
import io.ekfkawl.model.EventStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

import static com.kakaoscan.server.infrastructure.constants.ResponseMessages.SEARCH_ERROR_PING_PONG;
import static com.kakaoscan.server.infrastructure.constants.ResponseMessages.SEARCH_QUEUE_WAITING;
import static com.kakaoscan.server.infrastructure.redis.enums.Topics.SEARCH_EVENT_TOPIC;
import static io.ekfkawl.enums.EventStatusEnum.PROCESSING;
import static io.ekfkawl.enums.EventStatusEnum.WAITING;
import static java.lang.String.format;

@Log4j2
@Service
@RequiredArgsConstructor
public class SearchEventManagerService {

    private final EventStatusPort eventStatusPort;
    private final SearchEventPublisher searchEventPublisher;
    private final SearchInMemoryQueue queue;
    private final StompMessageDispatcher messageDispatcher;

    private static final int PROCESSING_TIMEOUT_SECONDS = 30;

    public boolean checkUserTurnAndNotify(SearchMessage searchMessage, SearchMessage peekSearchMessage) {
        boolean isUserTurn = searchMessage.getEmail().equals(peekSearchMessage.getEmail());
        if (!isUserTurn) {
            int waitingCount = queue.size() - 1;
            messageDispatcher.sendToUser(new SearchMessage(searchMessage.getEmail(), format(SEARCH_QUEUE_WAITING, waitingCount), false));
            return false;
        }
        return true;
    }

    public boolean removeTimeoutEventAndNotify(SearchMessage searchMessage) {
        LocalDateTime thresholdTime = LocalDateTime.now().minusSeconds(1).minusNanos(500000000);

        Optional<EventStatus> eventStatus = eventStatusPort.getEventStatus(searchMessage.getMessageId());

        if (isMessageTimedOut(searchMessage, thresholdTime, eventStatus)) {
            messageDispatcher.sendToUser(new SearchMessage(searchMessage.getEmail(), SEARCH_ERROR_PING_PONG, false));
            queue.remove(searchMessage.getMessageId());
            eventStatusPort.deleteEventStatus(searchMessage.getMessageId());

            log.info("remove timed out message: " + searchMessage.getEmail() + ", " + searchMessage.getContent());
            return true;
        }

        return false;
    }

    public void publishAndTraceEvent(SearchMessage peekSearchMessage) {
        Optional<EventStatus> optionalEventStatus = eventStatusPort.getEventStatus(peekSearchMessage.getMessageId());
        if (optionalEventStatus.isEmpty()) {
            searchEventPublisher.publish(SEARCH_EVENT_TOPIC, peekSearchMessage);
        }
    }

    private boolean isMessageTimedOut(SearchMessage searchMessage, LocalDateTime thresholdTime, Optional<EventStatus> optionalEventStatus) {
        return optionalEventStatus
                .map(eventStatus -> {
                    LocalDateTime eventStartedAt = searchMessage.getEventStartedAt();
                    return eventStartedAt != null && (
                            (eventStatus.getStatus() == WAITING && eventStartedAt.isBefore(thresholdTime)) ||
                            (eventStatus.getStatus() == PROCESSING && eventStartedAt.isBefore(LocalDateTime.now().minusSeconds(PROCESSING_TIMEOUT_SECONDS))));
                })
                .orElseGet(() -> {
                    LocalDateTime eventStartedAt = searchMessage.getEventStartedAt();
                    return eventStartedAt != null && eventStartedAt.isBefore(LocalDateTime.now().minusSeconds(PROCESSING_TIMEOUT_SECONDS));
                });
    }
}

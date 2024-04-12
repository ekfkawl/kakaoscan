package com.kakaoscan.server.application.service.websocket.search;

import com.kakaoscan.server.application.port.EventStatusPort;
import com.kakaoscan.server.application.service.websocket.EventPublishService;
import com.kakaoscan.server.application.service.websocket.StompMessageDispatcher;
import com.kakaoscan.server.domain.events.model.EventStatus;
import com.kakaoscan.server.domain.search.model.SearchMessage;
import com.kakaoscan.server.infrastructure.redis.enums.Topics;
import com.kakaoscan.server.infrastructure.websocket.queue.SearchInMemoryQueue;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.Optional;

import static com.kakaoscan.server.domain.events.enums.EventStatusEnum.PROCESSING;
import static com.kakaoscan.server.domain.events.enums.EventStatusEnum.WAITING;
import static com.kakaoscan.server.infrastructure.constants.ResponseMessages.SEARCH_ERROR_PING_PONG;
import static com.kakaoscan.server.infrastructure.constants.ResponseMessages.SEARCH_QUEUE_WAITING;
import static com.kakaoscan.server.infrastructure.redis.enums.Topics.EVENT_TRACE_TOPIC;
import static com.kakaoscan.server.infrastructure.redis.enums.Topics.SEARCH_EVENT_TOPIC;
import static java.lang.String.format;

@Log4j2
@Service
@RequiredArgsConstructor
public class SearchEventManagerService {

    private final EventStatusPort eventStatusPort;
    private final EventPublishService eventPublishService;
    private final SearchInMemoryQueue queue;
    private final StompMessageDispatcher messageDispatcher;

    private static final int PROCESSING_TIMEOUT_SECONDS = 20;

    public boolean checkUserTurnAndNotify(SearchMessage searchMessage, SearchMessage peekSearchMessage) {
        boolean isUserTurn = searchMessage.getEmail().equals(peekSearchMessage.getEmail());
        if (!isUserTurn) {
            int waitingCount = queue.size() - 1;
            messageDispatcher.sendToUser(new SearchMessage(searchMessage.getEmail(), format(SEARCH_QUEUE_WAITING, waitingCount), true));
            return false;
        }
        return true;
    }

    public boolean removeTimeoutEvent(SearchMessage peekSearchMessage) {
        boolean isRemovedPeek = false;
        LocalDateTime thresholdTime = LocalDateTime.now().minusSeconds(1).minusNanos(500000000);

        Iterator<SearchMessage> iterator = queue.iterator();
        while (iterator.hasNext()) {
            SearchMessage next = iterator.next();
            Optional<EventStatus> eventStatus = eventStatusPort.getEventStatus(next.getMessageId());

            if (isMessageTimedOut(next, thresholdTime, eventStatus)) {
                isRemovedPeek = shouldRemovePeek(next, peekSearchMessage);

                messageDispatcher.sendToUser(new SearchMessage(next.getEmail(), SEARCH_ERROR_PING_PONG, false));
                iterator.remove();
                eventStatusPort.deleteEventStatus(next.getMessageId());

                log.info("remove timed out message: " + next.getEmail() + ", " + next.getContent());
            }
        }

        return isRemovedPeek;
    }

    public void publishAndTraceEvent(SearchMessage peekSearchMessage) {
        Optional<EventStatus> optionalEventStatus = eventStatusPort.getEventStatus(peekSearchMessage.getMessageId());

        Topics topic = optionalEventStatus.isEmpty() ? SEARCH_EVENT_TOPIC : EVENT_TRACE_TOPIC;
        eventPublishService.publishSearchEvent(topic, peekSearchMessage);
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

    private boolean shouldRemovePeek(SearchMessage currentSearchMessage, SearchMessage peekSearchMessage) {
        return currentSearchMessage.getEmail().equals(peekSearchMessage.getEmail());
    }
}

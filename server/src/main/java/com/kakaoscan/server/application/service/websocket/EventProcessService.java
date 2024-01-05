package com.kakaoscan.server.application.service.websocket;

import com.kakaoscan.server.application.port.EventStatusPort;
import com.kakaoscan.server.domain.events.model.EventStatus;
import com.kakaoscan.server.domain.search.model.Message;
import com.kakaoscan.server.domain.search.queue.QueueAggregate;
import com.kakaoscan.server.infrastructure.redis.enums.Topics;
import lombok.RequiredArgsConstructor;
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

@Service
@RequiredArgsConstructor
public class EventProcessService {

    private final EventStatusPort eventStatusPort;
    private final EventPublishService eventPublishService;
    private final QueueAggregate queue;
    private final StompMessageDispatcher messageDispatcher;

    public boolean checkUserTurnAndNotify(Message message, Message peekMessage) {
        boolean isUserTurn = message.getEmail().equals(peekMessage.getEmail());
        if (!isUserTurn) {
            int waitingCount = queue.size() - 1;
            messageDispatcher.sendToUser(new Message(message.getEmail(), format(SEARCH_QUEUE_WAITING, waitingCount)));
            return false;
        }
        return true;
    }

    public boolean removeTimeoutEvent(Message peekMessage) {
        boolean isRemovedPeek  = false;
        LocalDateTime thresholdTime = LocalDateTime.now().minusSeconds(1);

        Iterator<Message> iterator = queue.iterator();
        while (iterator.hasNext()) {
            Message next = iterator.next();
            Optional<EventStatus> eventStatus = eventStatusPort.getEventStatus(next.getMessageId());

            if (eventStatus.isPresent() && isMessageTimedOut(next, thresholdTime, eventStatus.get())) {
                isRemovedPeek = shouldRemovePeek(isRemovedPeek, next, peekMessage);

                messageDispatcher.sendToUser(new Message(next.getEmail(), SEARCH_ERROR_PING_PONG, false));
                iterator.remove();
            }
        }

        return isRemovedPeek;
    }

    public void publishAndTraceEvent(Message peekMessage) {
        Optional<EventStatus> optionalEventStatus = eventStatusPort.getEventStatus(peekMessage.getMessageId());

        Topics topic = optionalEventStatus.isEmpty() ? SEARCH_EVENT_TOPIC : EVENT_TRACE_TOPIC;
        eventPublishService.publishSearchEvent(topic, peekMessage);
    }

    private boolean isMessageTimedOut(Message message, LocalDateTime thresholdTime, EventStatus eventStatus) {
        return eventStatus.getStatus() == WAITING && message.getCreatedAt().isBefore(thresholdTime) ||
               eventStatus.getStatus() == PROCESSING && message.getCreatedAt().isBefore(LocalDateTime.now().minusSeconds(10));
    }

    private boolean shouldRemovePeek(boolean currentStatus, Message currentMessage, Message peekMessage) {
        return !currentStatus && currentMessage.getEmail().equals(peekMessage.getEmail());
    }
}

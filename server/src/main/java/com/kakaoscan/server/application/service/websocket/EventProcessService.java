package com.kakaoscan.server.application.service.websocket;

import com.kakaoscan.server.application.port.EventStatusPort;
import com.kakaoscan.server.domain.events.model.EventStatus;
import com.kakaoscan.server.domain.search.model.ProfileMessage;
import com.kakaoscan.server.infrastructure.redis.enums.Topics;
import com.kakaoscan.server.infrastructure.websocket.queue.ProfileInMemoryQueue;
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
    private final ProfileInMemoryQueue queue;
    private final StompMessageDispatcher messageDispatcher;

    public boolean checkUserTurnAndNotify(ProfileMessage profileMessage, ProfileMessage peekProfileMessage) {
        boolean isUserTurn = profileMessage.getEmail().equals(peekProfileMessage.getEmail());
        if (!isUserTurn) {
            int waitingCount = queue.size() - 1;
            messageDispatcher.sendToUser(new ProfileMessage(profileMessage.getEmail(), format(SEARCH_QUEUE_WAITING, waitingCount)));
            return false;
        }
        return true;
    }

    public boolean removeTimeoutEvent(ProfileMessage peekProfileMessage) {
        boolean isRemovedPeek = false;
        LocalDateTime thresholdTime = LocalDateTime.now().minusSeconds(1);

        Iterator<ProfileMessage> iterator = queue.iterator();
        while (iterator.hasNext()) {
            ProfileMessage next = iterator.next();
            Optional<EventStatus> eventStatus = eventStatusPort.getEventStatus(next.getMessageId());

            if (eventStatus.isPresent() && isMessageTimedOut(next, thresholdTime, eventStatus.get())) {
                isRemovedPeek = shouldRemovePeek(next, peekProfileMessage);

                messageDispatcher.sendToUser(new ProfileMessage(next.getEmail(), SEARCH_ERROR_PING_PONG, false, false));
                iterator.remove();
            }
        }

        return isRemovedPeek;
    }

    public void publishAndTraceEvent(ProfileMessage peekProfileMessage) {
        Optional<EventStatus> optionalEventStatus = eventStatusPort.getEventStatus(peekProfileMessage.getMessageId());

        Topics topic = optionalEventStatus.isEmpty() ? SEARCH_EVENT_TOPIC : EVENT_TRACE_TOPIC;
        eventPublishService.publishSearchEvent(topic, peekProfileMessage);
    }

    private boolean isMessageTimedOut(ProfileMessage profileMessage, LocalDateTime thresholdTime, EventStatus eventStatus) {
        return eventStatus.getStatus() == WAITING && profileMessage.getEventStartedAt().isBefore(thresholdTime) ||
               eventStatus.getStatus() == PROCESSING && profileMessage.getEventStartedAt().isBefore(LocalDateTime.now().minusSeconds(15));
    }

    private boolean shouldRemovePeek(ProfileMessage currentProfileMessage, ProfileMessage peekProfileMessage) {
        return currentProfileMessage.getEmail().equals(peekProfileMessage.getEmail());
    }
}

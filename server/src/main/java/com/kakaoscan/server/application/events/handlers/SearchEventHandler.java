package com.kakaoscan.server.application.events.handlers;

import com.kakaoscan.server.application.port.EventStatusPort;
import com.kakaoscan.server.application.port.PointPort;
import com.kakaoscan.server.application.service.websocket.StompMessageDispatcher;
import com.kakaoscan.server.domain.events.model.EventStatus;
import com.kakaoscan.server.domain.events.model.SearchEvent;
import com.kakaoscan.server.domain.search.model.SearchMessage;
import com.kakaoscan.server.infrastructure.events.processor.AbstractEventProcessor;
import com.kakaoscan.server.infrastructure.service.RateLimitService;
import com.kakaoscan.server.infrastructure.websocket.queue.SearchInMemoryQueue;
import io.github.bucket4j.Bucket;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Optional;

import static com.kakaoscan.server.domain.events.enums.EventStatusEnum.*;
import static com.kakaoscan.server.infrastructure.constants.ResponseMessages.*;

@Component
@RequiredArgsConstructor
@Log4j2
public class SearchEventHandler extends AbstractEventProcessor<SearchEvent> {
    private final EventStatusPort eventStatusPort;
    private final SearchInMemoryQueue queue;
    private final StompMessageDispatcher messageDispatcher;
    private final RateLimitService rateLimitService;
    private final PointPort pointPort;

    @Value("${search.profile.cost}")
    private int searchCost;

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
                if (SEARCH_INVALID_PHONE_NUMBER.equals(status.getMessage())) {
                    Bucket bucket = rateLimitService.resolveBucket(event.getEmail(), 5, Duration.ofHours(2));
                    if (!bucket.tryConsume(1)) {
                        log.info("too many invalid phone number search: {}", event.getEmail());
                    }
                }
                queue.remove(event.getEventId());
                yield status.getMessage();
            }
        };
        boolean hasNext = status.getStatus() == WAITING || status.getStatus() == PROCESSING;
        messageDispatcher.sendToUser(new SearchMessage(event.getEmail(), responseMessage, hasNext, status.getStatus() == SUCCESS));

        if (status.getStatus() == SUCCESS && !hasNext) {
            int cachePoints = pointPort.getPointsFromCache(event.getEmail());
            pointPort.cachePoints(event.getEmail(), cachePoints - searchCost);

            try {
                if (pointPort.deductPoints(event.getEmail(), searchCost)) {
                    log.info("deduct points: {} ({}-{}={})", event.getEmail(), cachePoints, searchCost, cachePoints - searchCost);
                }
            } catch (Exception e) {
                log.error("error deducting points user: {}, {}", event.getEmail(), e.getMessage(), e);
            }
        }
    }
}
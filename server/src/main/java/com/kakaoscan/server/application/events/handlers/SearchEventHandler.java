package com.kakaoscan.server.application.events.handlers;

import com.kakaoscan.server.application.port.EventStatusPort;
import com.kakaoscan.server.application.port.PointPort;
import com.kakaoscan.server.application.service.websocket.StompMessageDispatcher;
import com.kakaoscan.server.domain.events.model.EventStatus;
import com.kakaoscan.server.domain.events.model.SearchEvent;
import com.kakaoscan.server.domain.search.entity.SearchHistory;
import com.kakaoscan.server.domain.search.model.SearchMessage;
import com.kakaoscan.server.domain.search.model.SearchResult;
import com.kakaoscan.server.domain.user.repository.SearchRepository;
import com.kakaoscan.server.domain.user.repository.UserRepository;
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
import static com.kakaoscan.server.infrastructure.serialization.JsonDeserialize.deserialize;
import static com.kakaoscan.server.infrastructure.serialization.JsonSerialize.serialize;

@Component
@RequiredArgsConstructor
@Log4j2
public class SearchEventHandler extends AbstractEventProcessor<SearchEvent> {
    private final EventStatusPort eventStatusPort;
    private final SearchInMemoryQueue queue;
    private final StompMessageDispatcher messageDispatcher;
    private final RateLimitService rateLimitService;
    private final PointPort pointPort;
    private final UserRepository userRepository;
    private final SearchRepository searchRepository;

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

        if (deductPoints(event.getEmail(), status)) {
            SearchResult searchResult = deserialize(responseMessage, SearchResult.class);
            saveSearchData(event.getEmail(), event.getPhoneNumber(), serialize(searchResult));
        }
    }

    private boolean deductPoints(String userId, EventStatus status) {
        if (status.getStatus() != SUCCESS) {
            return false;
        }

        int cachePoints = pointPort.getPointsFromCache(userId);
        pointPort.cachePoints(userId, cachePoints - searchCost);

        try {
            if (pointPort.deductPoints(userId, searchCost)) {
                log.info("deduct points: {} ({}-{}={})", userId, cachePoints, searchCost, cachePoints - searchCost);
                return true;
            }
        } catch (Exception e) {
            log.error("error deducting points user: {}, {}", userId, e.getMessage(), e);
        }

        return false;
    }

    private void saveSearchData(String userId, String targetPhoneNumber, String data) {
        userRepository.findByEmail(userId).ifPresent(user -> {
            searchRepository.save(SearchHistory.builder()
                    .user(user)
                    .targetPhoneNumber(targetPhoneNumber)
                    .data(data)
                    .build());
        });
    }
}
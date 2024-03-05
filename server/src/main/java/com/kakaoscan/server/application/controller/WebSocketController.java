package com.kakaoscan.server.application.controller;

import com.kakaoscan.server.application.exception.EmptyQueueException;
import com.kakaoscan.server.application.port.EventStatusPort;
import com.kakaoscan.server.application.port.PhoneNumberCachePort;
import com.kakaoscan.server.application.port.PointPort;
import com.kakaoscan.server.application.service.websocket.StompMessageDispatcher;
import com.kakaoscan.server.application.service.websocket.search.SearchEventManagerService;
import com.kakaoscan.server.application.service.websocket.search.SearchMessageService;
import com.kakaoscan.server.application.service.websocket.search.SearchQueueService;
import com.kakaoscan.server.domain.events.model.EventStatus;
import com.kakaoscan.server.domain.point.model.PointMessage;
import com.kakaoscan.server.domain.search.model.SearchMessage;
import com.kakaoscan.server.infrastructure.service.RateLimitService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import java.security.Principal;
import java.util.ConcurrentModificationException;
import java.util.Optional;

import static com.kakaoscan.server.infrastructure.constants.ResponseMessages.*;

@Controller
@RequiredArgsConstructor
public class WebSocketController {
    private final SearchQueueService searchQueueService;
    private final SearchMessageService searchMessageService;
    private final SearchEventManagerService searchEventManagerService;
    private final PhoneNumberCachePort phoneNumberCachePort;
    private final StompMessageDispatcher messageDispatcher;
    private final RateLimitService rateLimitService;
    private final EventStatusPort eventStatusPort;
    private final PointPort pointPort;

    @EventListener
    public void handleWebSocketConnectListener(SessionSubscribeEvent event) {
        if (event.getUser() != null) {
            Optional<SearchMessage> optionalMessage = searchQueueService.findMessage(event.getUser().getName());
            optionalMessage.ifPresent(message -> {
                Optional<EventStatus> eventStatus = eventStatusPort.getEventStatus(message.getMessageId());
                if (eventStatus.isPresent()) {
                    messageDispatcher.sendToUser(message);
                }
            });
        }
    }

    @MessageMapping("/search")
    public void handleSearchProfile(Principal principal, SearchMessage.OriginMessage originMessage) {
        SearchMessage message = searchMessageService.createSearchMessage(principal, originMessage);

        if (!searchMessageService.validatePoints(message)) {
            messageDispatcher.sendToUser(new SearchMessage(message.getEmail(), NOT_ENOUGH_POINTS, false, false));
            return;
        }

        if (phoneNumberCachePort.isInvalidPhoneNumberCached(message.getContent())) {
            messageDispatcher.sendToUser(new SearchMessage(message.getEmail(), SEARCH_INVALID_PHONE_NUMBER, false, false));
            return;
        }

        if (rateLimitService.isBucketFull(message.getEmail())) {
            messageDispatcher.sendToUser(new SearchMessage(message.getEmail(), SEARCH_TOO_MANY_INVALID_PHONE_NUMBER, false, false));
            return;
        }

        Optional<SearchMessage> optionalPeekMessage = searchQueueService.enqueueAndPeekNext(message);
        if (optionalPeekMessage.isEmpty()) {
            throw new EmptyQueueException("websocket message queue is empty");
        }

        boolean isUserTurn = searchEventManagerService.checkUserTurnAndNotify(message, optionalPeekMessage.get());
        if (isUserTurn && !searchEventManagerService.removeTimeoutEvent(optionalPeekMessage.get())) {
            searchEventManagerService.publishAndTraceEvent(optionalPeekMessage.get());
        }
    }

    @MessageMapping("/points")
    public void handlePointBalance(Principal principal) {
        try {
            int points = pointPort.getPointsFromCache(principal.getName());
            messageDispatcher.sendToUser(new PointMessage(principal.getName(), points));
        } catch (NullPointerException | ConcurrentModificationException e) {
            messageDispatcher.sendToUser(new PointMessage(principal.getName(), -1, LOADING_POINTS_BALANCE));
        }
    }

}

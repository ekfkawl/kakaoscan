package com.kakaoscan.server.application.controller;

import com.kakaoscan.server.application.exception.EmptyQueueException;
import com.kakaoscan.server.application.port.CacheStorePort;
import com.kakaoscan.server.application.port.EventStatusPort;
import com.kakaoscan.server.application.service.PointService;
import com.kakaoscan.server.application.service.websocket.StompMessageDispatcher;
import com.kakaoscan.server.application.service.websocket.search.SearchEventManagerService;
import com.kakaoscan.server.application.service.websocket.search.SearchMessageService;
import com.kakaoscan.server.domain.events.model.EventStatus;
import com.kakaoscan.server.domain.search.model.InvalidPhoneNumber;
import com.kakaoscan.server.domain.search.model.SearchMessage;
import com.kakaoscan.server.infrastructure.cache.CacheUpdateObserver;
import com.kakaoscan.server.infrastructure.security.model.SimplePrincipal;
import com.kakaoscan.server.infrastructure.service.RateLimitService;
import com.kakaoscan.server.infrastructure.websocket.queue.SearchInMemoryQueue;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import java.security.Principal;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static com.kakaoscan.server.infrastructure.constants.RedisKeyPrefixes.INVALID_PHONE_NUMBER_KEY_PREFIX;
import static com.kakaoscan.server.infrastructure.constants.ResponseMessages.*;

@Controller
@RequiredArgsConstructor
public class WebSocketController {
    private final SearchMessageService searchMessageService;
    private final SearchEventManagerService searchEventManagerService;
    private final CacheStorePort<InvalidPhoneNumber> cacheStorePort;
    private final StompMessageDispatcher messageDispatcher;
    private final RateLimitService rateLimitService;
    private final EventStatusPort eventStatusPort;
    private final SearchInMemoryQueue queue;
    private final CacheUpdateObserver cacheUpdateObserver;
    private final PointService pointService;

    private static final Map<String, Set<String>> sessionSubscriptions = new ConcurrentHashMap<>();

    @MessageMapping("/search")
    public void handleSearchProfile(Principal principal, SearchMessage.OriginMessage originMessage) {
        SearchMessage message = searchMessageService.createSearchMessage(principal, originMessage);

        boolean isInvalidPhoneNumberCached = cacheStorePort.containsKey(INVALID_PHONE_NUMBER_KEY_PREFIX + message.getContent(), InvalidPhoneNumber.class);
        if (isInvalidPhoneNumberCached) {
            messageDispatcher.sendToUser(new SearchMessage(message.getEmail(), SEARCH_INVALID_PHONE_NUMBER, false));
            return;
        }

        if (!searchMessageService.canAttemptNumberSearch(message)) {
            messageDispatcher.sendToUser(new SearchMessage(message.getEmail(), MAX_DAILY_NEW_NUMBER_SEARCH, false));
            return;
        }

        if (!searchMessageService.validatePoints(message)) {
            messageDispatcher.sendToUser(new SearchMessage(message.getEmail(), NOT_ENOUGH_POINTS, false));
            return;
        }

        if (rateLimitService.isBucketFull(message.getEmail())) {
            messageDispatcher.sendToUser(new SearchMessage(message.getEmail(), SEARCH_TOO_MANY_INVALID_PHONE_NUMBER, false));
            return;
        }

        if (!queue.contains(message.getEmail())) {
            queue.add(message);
        }

        Optional<SearchMessage> optionalPeekMessage = queue.peek();
        if (optionalPeekMessage.isEmpty()) {
            throw new EmptyQueueException("websocket message queue is empty");
        }

        if (optionalPeekMessage.get().getEventStartedAt() == null) {
            optionalPeekMessage.get().setEventStartedAt();
        }

        boolean removedMessage = searchEventManagerService.removeTimeoutEventAndNotify(optionalPeekMessage.get());
        boolean isUserTurn = searchEventManagerService.checkUserTurnAndNotify(message, optionalPeekMessage.get());
        if (!removedMessage && isUserTurn) {
            searchEventManagerService.publishAndTraceEvent(optionalPeekMessage.get());
        }
    }

    @EventListener
    public void handleWebSocketConnectListener(SessionSubscribeEvent event) {
        String sessionId = event.getMessage().getHeaders().get(SimpMessageHeaderAccessor.SESSION_ID_HEADER, String.class);
        String destination = event.getMessage().getHeaders().get(SimpMessageHeaderAccessor.DESTINATION_HEADER, String.class);

        if (!"/user/queue/message/heartbeat".equals(destination)) {
            return;
        }

        if (sessionId != null) {
            if (sessionSubscriptions.containsKey(sessionId) && sessionSubscriptions.get(sessionId).contains(destination)) {
                return;
            }
            sessionSubscriptions.computeIfAbsent(sessionId, k -> new HashSet<>()).add(destination);
        }

        continueSearchEvent(event);
    }

    @MessageMapping("/points")
    public void handlePointBalance(Principal principal) {
        cacheUpdateObserver.update(principal.getName(), pointService.getPoints(principal.getName()));
    }

    @MessageMapping("/heartbeat")
    @SendToUser("/queue/message/heartbeat")
    public String handleSearchProfileHeartbeat() {
        queue.peek().ifPresent(searchMessage -> {
            if (searchMessage.getEventStartedAt() == null) {
                handleSearchProfile(new SimplePrincipal(searchMessage.getEmail()), new SearchMessage.OriginMessage(searchMessage.getContent(), searchMessage.isId()));
            }else {
                searchEventManagerService.removeTimeoutEventAndNotify(searchMessage);
            }
        });

        return "PONG";
    }

    private void continueSearchEvent(SessionSubscribeEvent event) {
        if (event.getUser() == null) {
            return;
        }

        Optional<SearchMessage> optionalMessage = searchMessageService.findSearchMessage(event.getUser().getName());
        optionalMessage.ifPresent(message -> {
            boolean removedMessage = searchEventManagerService.removeTimeoutEventAndNotify(message);
            if (removedMessage) {
                return;
            }

            Optional<EventStatus> eventStatus = eventStatusPort.getEventStatus(message.getMessageId());
            if (eventStatus.isPresent()) {
                switch (eventStatus.get().getStatus()) {
                    case WAITING, PROCESSING:
                        SearchMessage searchMessage = new SearchMessage(message.getEmail(), String.format(SEARCH_CONTINUE, eventStatus.get().getMessage()), false);
                        messageDispatcher.sendToUser(searchMessage);
                }
            }else {
                queue.peek().ifPresent(searchMessage -> {
                    searchEventManagerService.checkUserTurnAndNotify(message, searchMessage);
                });
            }
        });
    }
}

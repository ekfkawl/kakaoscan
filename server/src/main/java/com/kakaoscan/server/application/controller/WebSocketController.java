package com.kakaoscan.server.application.controller;

import com.kakaoscan.server.application.exception.EmptyQueueException;
import com.kakaoscan.server.application.port.EventStatusPort;
import com.kakaoscan.server.application.port.PhoneNumberCachePort;
import com.kakaoscan.server.application.port.PointPort;
import com.kakaoscan.server.application.service.MessageService;
import com.kakaoscan.server.application.service.websocket.EventProcessService;
import com.kakaoscan.server.application.service.websocket.MessageQueueService;
import com.kakaoscan.server.application.service.websocket.StompMessageDispatcher;
import com.kakaoscan.server.domain.events.model.EventStatus;
import com.kakaoscan.server.domain.point.model.PointMessage;
import com.kakaoscan.server.domain.search.model.ProfileMessage;
import com.kakaoscan.server.infrastructure.service.RateLimitService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import java.security.Principal;
import java.util.Optional;

import static com.kakaoscan.server.infrastructure.constants.ResponseMessages.*;

@Controller
@RequiredArgsConstructor
public class WebSocketController {
    private final MessageQueueService messageQueueService;
    private final MessageService messageService;
    private final EventProcessService eventProcessService;
    private final PhoneNumberCachePort phoneNumberCachePort;
    private final StompMessageDispatcher messageDispatcher;
    private final RateLimitService rateLimitService;
    private final EventStatusPort eventStatusPort;
    private final PointPort pointPort;

    @EventListener
    public void handleWebSocketConnectListener(SessionSubscribeEvent event) {
        if (event.getUser() != null) {
            Optional<ProfileMessage> optionalMessage = messageQueueService.findMessage(event.getUser().getName());
            optionalMessage.ifPresent(message -> {
                Optional<EventStatus> eventStatus = eventStatusPort.getEventStatus(message.getMessageId());
                if (eventStatus.isPresent()) {
                    messageDispatcher.sendToUser(message);
                }
            });
        }
    }

    @MessageMapping("/profile")
    public void handleProfile(Principal principal, ProfileMessage.OriginMessage originMessage) {
        ProfileMessage profileMessage = messageService.createProfileMessage(principal, originMessage);

        if (phoneNumberCachePort.isInvalidPhoneNumberCached(profileMessage.getContent())) {
            messageDispatcher.sendToUser(new ProfileMessage(profileMessage.getEmail(), SEARCH_INVALID_PHONE_NUMBER, false, false));
            return;
        }

        if (rateLimitService.isBucketFull(profileMessage.getEmail())) {
            messageDispatcher.sendToUser(new ProfileMessage(profileMessage.getEmail(), SEARCH_TOO_MANY_INVALID_PHONE_NUMBER, false, false));
            return;
        }

        Optional<ProfileMessage> optionalPeekMessage = messageQueueService.enqueueAndPeekNext(profileMessage);
        if (optionalPeekMessage.isEmpty()) {
            throw new EmptyQueueException("websocket message queue is empty");
        }

        boolean isUserTurn = eventProcessService.checkUserTurnAndNotify(profileMessage, optionalPeekMessage.get());
        if (isUserTurn && !eventProcessService.removeTimeoutEvent(optionalPeekMessage.get())) {
            eventProcessService.publishAndTraceEvent(optionalPeekMessage.get());
        }
    }

    @MessageMapping("/points")
    public void handlePointBalance(Principal principal) {
        try {
            int points = pointPort.getPointsFromCache(principal.getName());
            messageDispatcher.sendToUser(new PointMessage(principal.getName(), points));
        } catch (NullPointerException e) {
            messageDispatcher.sendToUser(new PointMessage(principal.getName(), -1, LOADING_POINTS_BALANCE));
        }
    }

}

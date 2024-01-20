package com.kakaoscan.server.application.controller;

import com.kakaoscan.server.application.exception.EmptyQueueException;
import com.kakaoscan.server.application.port.EventStatusPort;
import com.kakaoscan.server.application.port.PhoneNumberCachePort;
import com.kakaoscan.server.application.service.MessageService;
import com.kakaoscan.server.application.service.websocket.EventProcessService;
import com.kakaoscan.server.application.service.websocket.MessageQueueService;
import com.kakaoscan.server.application.service.websocket.StompMessageDispatcher;
import com.kakaoscan.server.domain.events.model.EventStatus;
import com.kakaoscan.server.domain.search.model.Message;
import com.kakaoscan.server.infrastructure.service.RateLimitService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import java.security.Principal;
import java.util.Optional;

import static com.kakaoscan.server.infrastructure.constants.ResponseMessages.SEARCH_INVALID_PHONE_NUMBER;
import static com.kakaoscan.server.infrastructure.constants.ResponseMessages.SEARCH_TOO_MANY_INVALID_PHONE_NUMBER;

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

    @EventListener
    public void handleWebSocketConnectListener(SessionSubscribeEvent event) {
        if (event.getUser() != null) {
            Optional<Message> optionalMessage = messageQueueService.findMessage(event.getUser().getName());
            optionalMessage.ifPresent(message -> {
                Optional<EventStatus> eventStatus = eventStatusPort.getEventStatus(message.getMessageId());
                if (eventStatus.isPresent()) {
                    messageDispatcher.sendToUser(message);
                }
            });
        }
    }

    @MessageMapping("/send")
    public void handleMessage(Principal principal, Message.OriginMessage originMessage) {
        Message message = messageService.createMessage(principal, originMessage);

        if (phoneNumberCachePort.isInvalidPhoneNumberCached(message.getContent())) {
            messageDispatcher.sendToUser(new Message(message.getEmail(), SEARCH_INVALID_PHONE_NUMBER, false, false));
            return;
        }

        if (rateLimitService.isBucketFull(message.getEmail())) {
            messageDispatcher.sendToUser(new Message(message.getEmail(), SEARCH_TOO_MANY_INVALID_PHONE_NUMBER, false, false));
            return;
        }

        Optional<Message> optionalPeekMessage = messageQueueService.enqueueAndPeekNext(message);
        if (optionalPeekMessage.isEmpty()) {
            throw new EmptyQueueException("websocket message queue is empty");
        }

        boolean isUserTurn = eventProcessService.checkUserTurnAndNotify(message, optionalPeekMessage.get());
        if (isUserTurn && !eventProcessService.removeTimeoutEvent(optionalPeekMessage.get())) {
            eventProcessService.publishAndTraceEvent(optionalPeekMessage.get());
        }
    }
}

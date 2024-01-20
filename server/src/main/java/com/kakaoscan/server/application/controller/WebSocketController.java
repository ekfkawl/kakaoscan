package com.kakaoscan.server.application.controller;

import com.kakaoscan.server.application.exception.EmptyQueueException;
import com.kakaoscan.server.application.port.PhoneNumberCachePort;
import com.kakaoscan.server.application.service.MessageService;
import com.kakaoscan.server.application.service.websocket.EventProcessService;
import com.kakaoscan.server.application.service.websocket.MessageQueueService;
import com.kakaoscan.server.application.service.websocket.StompMessageDispatcher;
import com.kakaoscan.server.domain.search.model.Message;
import com.kakaoscan.server.infrastructure.service.RateLimitService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.SessionConnectedEvent;

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

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        // @TODO 연결시 useremail로 진행 중인 이벤트가 존재하는지 확인
        System.out.println("connect: " + event.getUser());
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

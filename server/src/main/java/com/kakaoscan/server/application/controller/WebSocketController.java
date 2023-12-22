package com.kakaoscan.server.application.controller;

import com.kakaoscan.server.application.service.MessageService;
import com.kakaoscan.server.domain.events.types.external.SearchEvent;
import com.kakaoscan.server.domain.search.model.Message;
import com.kakaoscan.server.infrastructure.redis.publisher.EventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import java.security.Principal;

import static com.kakaoscan.server.infrastructure.redis.enums.Topics.SEARCH_EVENT_TOPIC;

@Controller
@RequiredArgsConstructor
public class WebSocketController {

    private final MessageService messageService;
    private final EventPublisher eventPublisher;

    @MessageMapping("/send")
    public void sendMessage(Principal principal, Message.OriginMessage originMessage) {
        Message message = messageService.createMessage(principal, originMessage);

        eventPublisher.publish(SEARCH_EVENT_TOPIC.getTopic(), new SearchEvent(message.getEmail(), message.getContent(), "127.0.0.1"));
    }
}

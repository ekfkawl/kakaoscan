package com.kakaoscan.server.application.service.websocket;

import com.kakaoscan.server.domain.search.model.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StompMessageDispatcher {
    private final SimpUserRegistry simpUserRegistry;
    private final SimpMessagingTemplate messagingTemplate;

    public void sendToUser(Message message) {
        if (simpUserRegistry.getUser(message.getEmail()) != null) {
            messagingTemplate.convertAndSendToUser(message.getEmail(), "/queue/message", message);
        }
    }
}

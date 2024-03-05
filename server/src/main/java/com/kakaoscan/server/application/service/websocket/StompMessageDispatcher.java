package com.kakaoscan.server.application.service.websocket;

import com.kakaoscan.server.domain.point.model.PointMessage;
import com.kakaoscan.server.domain.websocket.model.MessageMetadata;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StompMessageDispatcher {
    private final SimpUserRegistry simpUserRegistry;
    private final SimpMessagingTemplate messagingTemplate;

    public <T extends MessageMetadata> void sendToUser(T message) {
        if (simpUserRegistry.getUser(message.getEmail()) != null) {

            String dest = "/queue/message/profile";
            if (message instanceof PointMessage) {
                dest = "/queue/message/point";
            }

            messagingTemplate.convertAndSendToUser(message.getEmail(), dest, message);
        }
    }
}

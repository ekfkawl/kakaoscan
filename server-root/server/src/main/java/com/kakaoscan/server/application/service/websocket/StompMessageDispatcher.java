package com.kakaoscan.server.application.service.websocket;

import com.kakaoscan.server.domain.point.model.PointMessage;
import com.kakaoscan.server.domain.websocket.model.MessageMetadata;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Service;

@Log4j2
@Service
@RequiredArgsConstructor
public class StompMessageDispatcher {
    private final SimpUserRegistry simpUserRegistry;
    private final SimpMessagingTemplate messagingTemplate;

    public <T extends MessageMetadata> void sendToUser(T message) {
        if (simpUserRegistry.getUser(message.getEmail()) != null) {
            String dest = "/queue/message/search";
            if (message instanceof PointMessage) {
                dest = "/queue/message/point";
            }
            messagingTemplate.convertAndSendToUser(message.getEmail(), dest, message);
        }
    }
}

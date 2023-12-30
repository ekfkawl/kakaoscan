package com.kakaoscan.server.application.service;

import com.kakaoscan.server.domain.search.model.Message;
import com.kakaoscan.server.infrastructure.exception.UserNotVerifiedException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
@RequiredArgsConstructor
public class MessageService {

    public Message createMessage(Principal principal, Message.OriginMessage originMessage) {
        if (principal != null) {
            return new Message(principal.getName(), originMessage.getContent());
        }else {
            throw new UserNotVerifiedException("websocket principal is empty");
        }
    }
}

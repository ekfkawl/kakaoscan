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
            final String phoneNumber = originMessage.getContent().trim().replace("-", "");
            if (phoneNumber.length() == 11 && phoneNumber.matches("\\d+")) {
                return new Message(principal.getName(), phoneNumber);
            }else {
                throw new IllegalArgumentException("message content is not a phone number format");
            }
        }else {
            throw new UserNotVerifiedException("websocket principal is empty");
        }
    }
}

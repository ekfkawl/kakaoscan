package com.kakaoscan.server.application.service;

import com.kakaoscan.server.domain.search.model.ProfileMessage;
import com.kakaoscan.server.infrastructure.exception.UserNotVerifiedException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
@RequiredArgsConstructor
public class MessageService {

    public ProfileMessage createProfileMessage(Principal principal, ProfileMessage.OriginMessage originMessage) {
        if (principal != null) {
            final String phoneNumber = originMessage.getContent().trim().replace("-", "");
            if (phoneNumber.length() == 11 && phoneNumber.matches("\\d+")) {
                return new ProfileMessage(principal.getName(), phoneNumber);
            }else {
                throw new IllegalArgumentException("message content is not a phone number format");
            }
        }else {
            throw new UserNotVerifiedException("websocket principal is empty");
        }
    }
}

package com.kakaoscan.server.application.service;

import com.kakaoscan.server.domain.search.model.Message;
import com.kakaoscan.server.infrastructure.security.JwtTokenProvider;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.SignatureException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final JwtTokenProvider jwtTokenProvider;

    public Message createMessage(Principal principal, Message.OriginMessage originMessage) {
        if (principal != null) {
            return new Message(principal.getName(), originMessage.getContent());
        }

        if (originMessage.getToken() == null || !jwtTokenProvider.validateAccessToken(originMessage.getToken())) {
            throw new JwtException("invalid or missing token");
        }

        Authentication authentication;
        try {
            authentication = jwtTokenProvider.getAuthentication(originMessage.getToken());
        } catch (SignatureException e) {
            throw new JwtException("authentication failed");
        }

        return new Message(authentication.getName(), originMessage.getContent());
    }
}

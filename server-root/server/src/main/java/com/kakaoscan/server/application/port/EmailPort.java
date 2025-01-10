package com.kakaoscan.server.application.port;

import com.kakaoscan.server.infrastructure.email.template.EmailTemplate;

import java.util.Map;

public interface EmailPort {
    <T extends EmailTemplate> void send(T emailTemplate, Map<String, Object> variables);
}

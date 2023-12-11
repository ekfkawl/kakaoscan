package com.kakaoscan.server.application.port;

import com.kakaoscan.server.application.dto.EmailTemplate;

public interface EmailPort {
    <T extends EmailTemplate> void send(T emailTemplate);
}

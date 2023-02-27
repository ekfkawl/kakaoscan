package com.kakaoscan.profile.domain.service;

import com.kakaoscan.profile.domain.model.EmailMessage;

public interface SendMail {
    void send(EmailMessage emailMessage, String template);
}
